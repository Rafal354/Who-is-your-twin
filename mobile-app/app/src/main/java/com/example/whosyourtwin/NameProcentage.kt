package com.example.whosyourtwin

import android.os.Parcel
import android.os.Parcelable

data class NamePercentage(
    val name: String,
    val percentage: Int
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(percentage)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NamePercentage> {
        override fun createFromParcel(parcel: Parcel): NamePercentage {
            return NamePercentage(parcel)
        }

        override fun newArray(size: Int): Array<NamePercentage?> {
            return arrayOfNulls(size)
        }
    }
}
