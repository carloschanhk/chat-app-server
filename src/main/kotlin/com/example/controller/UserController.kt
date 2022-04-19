package com.example.controller

import com.example.data.UserDataSource
import com.example.data.model.User
import com.example.data.request.SignUpRequest
import com.example.util.Response
import com.example.util.exception.MissingParameterException
import io.ktor.http.*

class UserController(private val userDataSource: UserDataSource) {
     fun signupUser(request: SignUpRequest): Response<User> {
        return try {
            userDataSource.signUpUser(request.name, request.password)
        } catch (e: Exception) {
            Response.Error(e)
        }
    }

     fun loginUser(queryParameters: Parameters): Response<User> {
        return try {
            val name = queryParameters["name"]
            val password = queryParameters["password"]
            if (name.isNullOrEmpty() || password.isNullOrEmpty()) throw MissingParameterException()
            userDataSource.loginUser(name, password)
        } catch (e: Exception) {
            Response.Error(e)
        }
    }

     fun searchUserByName(queryParameters: Parameters): Response<List<User>>{
        return try {
            val name = queryParameters["name"]
            if (name.isNullOrEmpty()) throw MissingParameterException()
            userDataSource.searchUserByName(name)
        } catch (e: Exception){
            Response.Error(e)
        }
    }
}