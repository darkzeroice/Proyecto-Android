package com.chacanasoft.jorge.fluctuacion

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_registrar_productos.*
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.chacanasoft.jorge.fluctuacion.adapter.ProductoRecyclerViewAdapter
//import jdk.nashorn.internal.runtime.ECMAException.getException
//import android.support.test.orchestrator.junit.BundleJUnitUtils.getResult
//import org.junit.experimental.results.ResultMatchers.isSuccessful
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import java.util.HashMap


//import javax.swing.UIManager.put



class RegistrarProductosActivity : AppCompatActivity() {

    // Access a Cloud Firestore instance from your Activity
    private var db = FirebaseFirestore.getInstance()
    private var mAuth = FirebaseAuth.getInstance()


    private var productosList = mutableListOf<MiProducto>()
    private var mAdapter: ProductoRecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_productos)

        loadProductosList()


        btnAgregar.setOnClickListener(){
            fnCrearDatos()
        }

        btnBuscar.setOnClickListener(){
            fnBuscarDatos()
        }

        btnQuitar.setOnClickListener(){
            fnBorrarDatos()
        }

    }



    private fun fnCrearDatos() {
        val sNombre = txtProducto.getText().toString().trim({ it <= ' ' })
        val sDescripcion = txtDescripcion.getText().toString().trim({ it <= ' ' })

        // Create a new user with a first and last name
        val productos = HashMap<String, Any>()
        productos.put("nombre", sNombre)
        productos.put("descripcion", sDescripcion)


        // Add a new document with a generated ID
        db.collection("users").document(mAuth.uid.toString()).collection("productos").document(sNombre)
                .set(productos)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(this,R.string.save_product,Toast.LENGTH_SHORT).show()
                    //Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.id)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this,R.string.error_save_product,Toast.LENGTH_SHORT).show()
                    //Log.w("TAG", "Error adding document", e)
                }

        productosList.add(MiProducto("", sNombre, sDescripcion))
        mAdapter?.notifyDataSetChanged()
    }

    private fun fnBorrarDatos() {
        val sNombre = txtProducto.getText().toString().trim({ it <= ' ' })
        db.collection("users").document(mAuth.uid.toString()).collection("productos").document(sNombre)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this,R.string.delete_product,Toast.LENGTH_SHORT).show()
                    Log.d("TAG", "DocumentSnapshot successfully deleted!")
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this,R.string.error_delete_product,Toast.LENGTH_SHORT).show()
                    Log.w("TAG", "Error deleting document", e)
                }


        var iterator = productosList.iterator()
        var productFound = false
        while (iterator.hasNext() && !productFound){
            var producto = iterator.next()
            if (producto.nombre.equals(sNombre)){
                productFound = true
            }

            if (productFound){
                iterator.remove()
                mAdapter?.notifyDataSetChanged()
            }
        }

    }

    private fun fnBuscarDatos() {
        db.collection("users").document(mAuth.uid.toString()).collection("productos")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            Log.d("TAG", document.id + " => " + document.data)
                        }
                    } else {
                        //Log.d("TAG", "Error getting documents: ", task.exception)
                    }
                }
    }

    private fun fnBuscarProducto(sProducto: String) {
        val docRef : DocumentReference
        db.collection("productos")
                .whereEqualTo("nombre", sProducto)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            Log.d("TAG", document.id + " => " + document.data)
                        }
                    } else {
                        //Log.d("TAG", "Error getting documents: ", task.exception)
                    }
                }

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

                        mAdapter = ProductoRecyclerViewAdapter(productosList, this, db, true, null)
                        val mLayoutManager = LinearLayoutManager(applicationContext)
                        rvProductList.layoutManager = mLayoutManager
                        rvProductList.itemAnimator = DefaultItemAnimator()
                        rvProductList.adapter = mAdapter
                    } else {
                        Log.d("TAG", "Error getting documents: ", task.exception)
                    }
                }
    }


}
