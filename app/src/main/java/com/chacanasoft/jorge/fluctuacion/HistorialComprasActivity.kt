package com.chacanasoft.jorge.fluctuacion

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.chacanasoft.jorge.fluctuacion.adapter.ProductoRecyclerViewAdapter
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate.JOYFUL_COLORS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_historial_compras.*



class HistorialComprasActivity : AppCompatActivity() {

    private var producto:MiProducto? = null

    private var productosList = mutableListOf<BarEntry>()

    private var db = FirebaseFirestore.getInstance()
    private var mAuth = FirebaseAuth.getInstance()

    private var productosList2 = mutableListOf<MiProducto>()
    private var mAdapter: ProductoRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        producto = intent.getParcelableExtra("producto")
        loadProductosList()
        setContentView(R.layout.activity_historial_compras)
    }

    private fun loadStadistics(){
        //barchart.setUsePercentValues(true)
        //piechart.description.setEnabled(false)
        barchart.description.setEnabled(false)

        barchart.setExtraOffsets(5f, 10f, 5f, 5f)

        barchart.dragDecelerationFrictionCoef = 0.95f

        //barchart.setDrawHoleEnabled(true)
        //barchart.setHoleColor(Color.WHITE)
        //barchart.transparentCircleRadius = 61f

        var i : Float = 0f
        for (producto in productosList2){
            i = i + 1f
            //productosList.add(PieEntry(producto.precio.toFloat(), producto.nombre))
            productosList.add(BarEntry(i, producto.precio.toFloat()))
        }


        //val dataset = PieDataSet(productosList, R.string.title_products.toString())
        val dataset = BarDataSet (productosList, R.string.title_products.toString())
        //dataset.sliceSpace = 3f
        //dataset.selectionShift = 5f

        dataset.colors = convertIntArrayToList(JOYFUL_COLORS)
        dataset.label = producto?.nombre

        val data = BarData(dataset)
        data.setValueTextSize(20f)
        data.setValueTextColor(Color.BLACK)

        barchart.data = data
    }

    private fun convertIntArrayToList(input: IntArray): List<Int> {

        val list = mutableListOf<Int>()
        for (i in input) {
            list.add(i)
        }
        return list

    }

    private fun loadProductosList() {

        db.collection("users").document(mAuth.uid.toString()).collection("compras")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            //Log.d("TAG", document.id + " => " + document.data)
                            //Recupero el documento
                            db.collection("users").document(mAuth.uid.toString())
                                    .collection("compras")
                                    .document(document.id)
                                    .collection("historial")
                                    .whereEqualTo("nombre", producto?.nombre)
                                    .get()
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            //Recupero los items
                                            for (document in task.result) {
                                                val prod = document.toObject<MiProducto>(MiProducto::class.java)
                                                //prod.id = document.id
                                                productosList2.add(prod)
                                            }
                                            loadStadistics()
                                        } else {
                                            Log.d("TAG", "Error getting documents: ", task.exception)
                                        }
                                    }
                        }
                    } else {
                        Log.d("TAG", "Error getting documents: ", task.exception)
                    }
        }
    }

}
