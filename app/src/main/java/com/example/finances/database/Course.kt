package com.example.finances.database
class Course (
    var id: Int = 0,
    var name: String? = null,
    var startDate: Long = 0,
    var endDate: Long = 0,
    var finished: Int = 0,
    var lessons: Int = 0,
    var lessonsCompleted: Int = 0)