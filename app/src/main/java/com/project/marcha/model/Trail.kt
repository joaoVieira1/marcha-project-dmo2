package com.project.marcha.model

import com.google.firebase.Timestamp

class Trail(
    private val trailName: String,
    private val userName: String,
    private val time: String,
    private val steps: Int,
    private val distance: Float,
    private val maxHeight: Float,
    private val data: String
) {

    public fun getTrailName(): String{
        return trailName
    }

    public fun getUserName(): String{
        return userName
    }

    public fun getTime(): String{
        return time
    }

    public fun getSteps(): Int{
        return steps
    }

    public fun getDistance(): Float{
        return distance
    }

    public fun getMaxHeight(): Float{
        return maxHeight
    }

    public fun getData(): String{
        return data
    }

}