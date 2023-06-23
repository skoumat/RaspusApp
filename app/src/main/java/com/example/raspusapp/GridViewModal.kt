package com.example.raspusapp

data class GridViewModal(val line: String, val file: String) : Comparable<Any> {
    override fun compareTo(other: Any): Int {
        TODO("Not yet implemented")
    }
}