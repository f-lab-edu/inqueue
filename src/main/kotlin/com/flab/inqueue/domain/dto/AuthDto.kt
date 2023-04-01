package com.flab.inqueue.domain.dto


data class AuthRequest(
   val eventId : String,
   val userId : String? = null
)

data class AuthResponse(
   val accessToken : String
)