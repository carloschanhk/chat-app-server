package com.example.routes

import com.example.controller.UserController
import com.example.data.request.SignUpRequest
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException
import org.jetbrains.exposed.exceptions.ExposedSQLException

fun Route.userRoute(userController: UserController) {
    route("/user") {
        get("/login") {
            try {
                val result = userController.loginUser(call.parameters)
                result.takeIfSuccess()?.let {
                    call.respond(status = HttpStatusCode.OK, message = it)
                } ?: throw result.takeError()
            } catch (e: Exception) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = e.message ?: "Something went wrong"
                )
            }

        }
        post("/signup") {
            try {
                val result = userController.signupUser(call.receive())
                result.takeIfSuccess()?.let {
                    call.respond(status = HttpStatusCode.Created, message = it)
                } ?: throw result.takeError()
            } catch (e: Exception) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = e.message ?: "Something went wrong"
                )
            }
        }
        get("search"){
            try {
                val result = userController.searchUserByName(call.parameters)
                result.takeIfSuccess()?.let {
                    call.respond(status = HttpStatusCode.Created, message = it)
                } ?: throw result.takeError()
            } catch (e: Exception) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = e.message ?: "Something went wrong"
                )
            }
        }
    }
}