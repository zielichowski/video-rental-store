package pl.zielichowski.rentalstore.common.api.exception

data class ApiError(
        var statusCode: Int,
        var clientMessage: String,
        var errors: List<String>
) {
    constructor(status: Int, clientMessage: String, error: String) :
            this(status, clientMessage, arrayListOf<String>(error))
}