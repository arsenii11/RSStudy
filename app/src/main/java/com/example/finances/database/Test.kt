package com.example.finances.database

class Test {
    var id = 0
    var name: String? = null
    var courseId = 0
    var date: Long = 0
    var weight = 0

    constructor() {}
    constructor(id: Int, name: String?, courseId: Int, date: Long, weight: Int) {
        this.id = id
        this.name = name
        this.courseId = courseId
        this.date = date
        this.weight = weight
    }
}