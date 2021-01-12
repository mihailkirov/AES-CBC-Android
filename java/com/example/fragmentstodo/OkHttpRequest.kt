package com.example.fragmentstodo

import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.lang.reflect.Array.get
import java.nio.charset.Charset

class OkHttpRequest(client:OkHttpClient) {
    var url_server:String = "http://10.0.2.2:62000/test1"
    private var client = OkHttpClient()

    init {
        this.client = client
    }


    fun GET(url: String, callback: Callback): Call {
        val request = Request.Builder()
            .url(url)
            .build()

        val call = client.newCall(request)
        call.enqueue(callback)
        return call
    }

    fun POST(url: String?, parameters: MutableList<Tache>, callback: Callback): Call {

        val builder = FormBody.Builder()
        for(t in parameters) {
            builder.add("nom", t.nom!!)
            builder.add("duree", t.duree.toString())
            builder.add("description", t.description!!)
            builder.add("categorie", t.categorie.toString())
            builder.add("place", t.place!!)
        }

        val formBody = builder.build()
        val request = Request.Builder()
            .url(url!!)
            .post(formBody)
            .build()

        // Asynchronous call
        val call = client.newCall(request)
        call.enqueue(callback)
        return call
    }

    /**
     * Sending a POST request to server
     */

    fun POSTFile(path:String, url: String, callback: Callback):Call{
        val fileContentType = MediaType.parse("File/*")
        val file = File(path)
        val requestBody = RequestBody.create(fileContentType, file)
        val request = Request.Builder().url(url).post(requestBody).build()
        val call = client.newCall(request)
        call.enqueue(callback)
        return call
    }

    fun sendSecret(key:String, iv:String, callback: Callback): Call? {
        val type = MediaType.parse("application/json; charset=utf-8")
        val sendObj = JSONObject()
        sendObj.put("iv", iv)
        sendObj.put("key", key)
        var body = RequestBody.create(type, sendObj.toString())
        val request = Request.Builder().url(this.url_server).post(body).build()
        val call = client.newCall(request)
        call.enqueue(callback)
        return call
    }


}