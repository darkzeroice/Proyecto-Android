package com.chacanasoft.jorge.fluctuacion

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.chacanasoft.jorge.fluctuacion.adapter.ProductoRecyclerViewAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_seleccionar_producto.*
import android.content.DialogInterface
import android.text.InputType
import android.widget.EditText



class SeleccionarProductoChartActivity : AppCompatActivity(), ProductoRecyclerViewAdapter.AdapterInterface{

    override fun onItemClicked(producto: MiProducto) {
        val intent = Intent(this, HistorialComprasActivity::class.java)
        intent.putExtra("producto", producto);
        this.startActivity(intent)
    }

    private var db = FirebaseFirestore.getInstance()
    private var mAuth = FirebaseAuth.getInstance()


    private var productosList = mutableListOf<MiProducto>()
    private var mAdapter: ProductoRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleccionar_producto)
        loadProductosList()
    }

    private fun loadProductosList() {
        db.collection("users").document(mAuth.uid.toString()).collection("productos")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //val productosList = mutableListOf<MiProducto>()

                        for (doc in task.result) {
                            val prod = doc.toObject<MiProducto>(MiProducto::class.java)
                            prod.id = doc.id
                            productosList.add(prod)
                        }

                        mAdapter = ProductoRecyclerViewAdapter(productosList, this, db, false, this)
                        val mLayoutManager = LinearLayoutManager(applicationContext)
                        rv_products.layoutManager = mLayoutManager
                        rv_products.itemAnimator = DefaultItemAnimator()
                        rv_products.adapter = mAdapter

                    } else {
                        Log.d("TAG", "Error getting documents: ", task.exception)
                    }
                }
    }
}
