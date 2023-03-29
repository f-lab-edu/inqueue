package com.flab.inqueue.dto


data class AuthRequest(
   val eventId : String,
   val userId : String? = null
)

data class AuthResponse(
   val accessToken : String
)