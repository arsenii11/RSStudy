package com.example.finances.database

class Event {
    var id = 0
    var eventId = 0
    var name: String? = null
    var courseId = 0
    var date: Long = 0
    var duration = 0f
    var eventType: EventType? = null

    enum class EventType {
        Lesson, Test
    }

    constructor() {}
    constructor(
        id: Int,
        eventId: Int,
        name: String?,
        courseId: Int,
        date: Long,
        duration: Float,
        eventType: EventType?
    ) {
        this.id = id
        this.eventId = eventId
        this.name = name
        this.courseId = courseId
        this.date = date
        this.duration = duration
        this.eventType = eventType
    }
}