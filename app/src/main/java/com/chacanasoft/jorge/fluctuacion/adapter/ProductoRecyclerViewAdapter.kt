package com.chacanasoft.jorge.fluctuacion.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.google.firebase.firestore.FirebaseFirestore

import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.chacanasoft.jorge.fluctuacion.*
import com.google.firebase.auth.FirebaseAuth

class ProductoRecyclerViewAdapter(
        private var productosList: MutableList<MiProducto>,
        private var context: Context,
        private val firestoreDB: FirebaseFirestore,
        private val editable: Boolean,
        private val listener: AdapterInterface?)
    : RecyclerView.Adapter<ProductoRecyclerViewAdapter.ViewHolder>() {

    private var mAuth = FirebaseAuth.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_producto, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val lsProducto = productosList[position]

        holder!!.title.text = lsProducto.nombre
        holder.content.text = lsProducto.descripcion

        if (context is SeleccionarProductoActivity){
            holder.edit.visibility = View.GONE
            holder.delete.visibility = View.GONE

        }else if(context is ComprasActivity){
            holder.edit.visibility = View.GONE
        }else if(context is SeleccionarProductoChartActivity){
            holder.edit.visibility = View.GONE
            holder.delete.visibility = View.GONE
        }

        holder.itemView.setOnClickListener(View.OnClickListener {
            if (listener != null){
                listener.onItemClicked(lsProducto)
            }
        })
        holder.edit.setOnClickListener { updateNote(lsProducto) }
        holder.delete.setOnClickListener {
            deleteNote(lsProducto.id!!, position)
        }
    }

    override fun getItemCount(): Int {
        return productosList.size
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        internal var title: TextView
        internal var content: TextView
        internal var edit: ImageView
        internal var delete: ImageView

        init {
            title = view.findViewById(R.id.tvTitle)
            content = view.findViewById(R.id.tvContent)

            edit = view.findViewById(R.id.ivEdit)
            delete = view.findViewById(R.id.ivDelete)
        }
    }

    private fun updateNote(producto: MiProducto) {
        val intent = Intent(context, RegistrarProductosActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("UpdateProductId", producto.id)
        intent.putExtra("UpdateProductName", producto.nombre)
        intent.putExtra("UpdateProductDescription", producto.descripcion)
        context.startActivity(intent)
    }

    private fun deleteNote(id: String, position: Int) {
        if (editable){
            fnBorrarDatos(productosList.get(position).nombre)
            /*firestoreDB.collection("notes")
                    .document(id)
                    .delete()
                    .addOnCompleteListener {*/
                        productosList.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, productosList.size)
                        //Toast.makeText(context, "Note has been deleted!", Toast.LENGTH_SHORT).show()
                    //}
        }else{
            productosList.removeAt(position)
            notifyItemRemoved(position)
        }

    }

    private fun fnBorrarDatos(nombreProducto: String) {
        firestoreDB.collection("users").document(mAuth.uid.toString()).collection("productos").document(nombreProducto)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(context,"Se ha eliminado correctamente el producto.",Toast.LENGTH_SHORT).show()
                    Log.d("TAG", "DocumentSnapshot successfully deleted!")
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context,"Ha ocurrido un error al eliminar el producto.",Toast.LENGTH_SHORT).show()
                    Log.w("TAG", "Error deleting document", e)
                }
    }


    interface AdapterInterface{
        fun onItemClicked(producto: MiProducto)
    }


}