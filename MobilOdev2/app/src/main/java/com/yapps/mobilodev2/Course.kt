package com.yapps.mobilodev2

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.Date

@Entity(tableName = "course_table")
data class Course (
    @PrimaryKey(autoGenerate = true)
    val id:Long = 0L,
    @ColumnInfo(name = "name")
    var courseName:String,
    @ColumnInfo(name = "courseId")
    var courseId:String,
    @ColumnInfo(name = "date")
    var date: String,
    @ColumnInfo(name = "numbers")
    var groupNumbers:Int,
    @ColumnInfo(name = "instructors")
    var instructors:String,
    @ColumnInfo(name = "students")
    var students:String,
    @ColumnInfo(name = "description")
    var description:String,
    @ColumnInfo(name = "creator")
    var creator:String = ""
): Parcelable{
    constructor(parcel:Parcel):this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeString(courseName)
        dest.writeString(courseId)
        dest.writeString(date)
        dest.writeInt(groupNumbers)
        dest.writeString(instructors)
        dest.writeString(students)
        dest.writeString(description)
        dest.writeString(creator)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR:Parcelable.Creator<Course>{
        override fun createFromParcel(source: Parcel): Course {
            return Course(source)
        }

        override fun newArray(size: Int): Array<Course?> {
            return arrayOfNulls(size)
        }

    }
}