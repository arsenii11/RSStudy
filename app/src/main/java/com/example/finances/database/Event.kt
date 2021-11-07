package com.example.finances.database
class Event(
    var id: Int = 0,
    var eventId: Int = 0,
    var name: String? = null,
    var courseId: Int = 0,
    var date: Long = 0,
    var duration: Float = 0f,
    var eventType: EventType? = null
){
    enum class EventType {
        Lesson, Test
    }
}
