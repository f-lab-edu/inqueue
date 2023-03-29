package com.flab.inqueue.dto


data class AuthRequest(
   val eventId : String
)

data class AuthResponse(
   val accessToken : String
)