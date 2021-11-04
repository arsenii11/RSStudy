package com.example.finances.database

class Course {
    var id = 0
    var name: String? = null
    var startDate: Long = 0
    var endDate: Long = 0
    var finished = 0
    var lessons = 0
    var lessonsCompleted = 0

    constructor() {}
    constructor(
        id: Int,
        name: String?,
        startDate: Long,
        endDate: Long,
        finished: Int,
        lessons: Int,
        lessonsCompleted: Int
    ) {
        this.id = id
        this.name = name
        this.startDate = startDate
        this.endDate = endDate
        this.finished = finished
        this.lessons = lessons
        this.lessonsCompleted = lessonsCompleted
    }
}