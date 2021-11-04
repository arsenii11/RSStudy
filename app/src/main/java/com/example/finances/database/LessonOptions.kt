package com.example.finances.database

class LessonOptions {
    var id = 0
    var lessonId = 0
    var calendarEventId = 0
    var isRepeatable = 0
    var repeatMode = 0
    var description: String? = null

    constructor() {}
    constructor(
        id: Int,
        lessonId: Int,
        calendarEventId: Int,
        isRepeatable: Int,
        repeatMode: Int,
        description: String?
    ) {
        this.id = id
        this.lessonId = lessonId
        this.calendarEventId = calendarEventId
        this.isRepeatable = isRepeatable
        this.repeatMode = repeatMode
        this.description = description
    }
}