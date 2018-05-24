package com.chacanasoft.jorge.fluctuacion

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Jorge on 01/03/2018.
 */



class MiCategoria(var MiId: String = "", var CategoriaNombre: String = "")

class MiProducto(

        var id: String? = "", var nombre: String = "", var descripcion: String = "", var precio: String = "", var fecha: String = "") : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(nombre)
        parcel.writeString(descripcion)
        parcel.writeString(precio)
        parcel.writeString(fecha)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MiProducto> {
        override fun createFromParcel(parcel: Parcel): MiProducto {
            return MiProducto(parcel)
        }

        override fun newArray(size: Int): Array<MiProducto?> {
            return arrayOfNulls(size)
        }
    }
}



