package com.chacanasoft.jorge.fluctuacion

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.chacanasoft.jorge.fluctuacion.adapter.ProductoRecyclerViewAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_compras.*
import java.text.SimpleDateFormat
import java.util.*


class ComprasActivity : AppCompatActivity() {

    private var db = FirebaseFirestore.getInstance()
    private var mAuth = FirebaseAuth.getInstance()

    private var productosList = mutableListOf<MiProducto>()
    private var mAdapter: ProductoRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compras)

        mAdapter = ProductoRecyclerViewAdapter(productosList, this, db, false, null)
        val mLayoutManager = LinearLayoutManager(applicationContext)
        rv_my_products.layoutManager = mLayoutManager
        rv_my_products.itemAnimator = DefaultItemAnimator()
        rv_my_products.adapter = mAdapter

        btn_add_product.setOnClickListener {
            val intent = Intent(this, SeleccionarProductoActivity::class.java)
            this.startActivityForResult(intent, 1)
        }

        btn_save.setOnClickListener {
            //guardar firebase
            fnCrearDatos()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1){
            if (resultCode == Activity.RESULT_OK){
                var producto: MiProducto? = data?.getParcelableExtra("producto")
                if (producto != null){
                    productosList.add(producto)
                    mAdapter?.notifyDataSetChanged()
                }

            }
        }

    }


    private fun fnCrearDatos() {

        if (!productosList.isEmpty()){
            // Create a new user with a first and last name
            for (producto in productosList){
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime())
                val compra = HashMap<String, Any>()
                compra.put("fecha", timeStamp)

                // Add a new document with a generated ID
                db.collection("users").document(mAuth.uid.toString()).collection("compras").document(timeStamp)
                        .set(compra)
                        .addOnSuccessListener { documentReference ->
                            Toast.makeText(this,R.string.save_sold_product, Toast.LENGTH_SHORT).show()
                            // Add a new document with a generated ID
                            compra.put("precio", producto.precio)
                            compra.put("nombre", producto.nombre)

                            db.collection("users").document(mAuth.uid.toString()).collection("compras").document(timeStamp).collection("historial").document(producto.nombre)
                                    .set(compra)
                                    .addOnSuccessListener { documentReference ->
                                        //Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.id)


                                    }
                                    .addOnFailureListener { e ->
                                        //Log.w("TAG", "Error adding document", e)
                                    }

                            //Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.id)
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this,R.string.error_save_sold_product, Toast.LENGTH_SHORT).show()
                            //Log.w("TAG", "Error adding document", e)
                        }


            }

            finish()

        }
    }
}
