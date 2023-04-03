package com.flab.inqueue.domain.customer.exception

class CustomerNotFoundException(
    clientId: String
) : RuntimeException("Customer(clientId=${clientId}) not Found")