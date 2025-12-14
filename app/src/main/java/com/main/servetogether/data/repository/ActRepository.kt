package com.main.servetogether.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ActRepository {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy {FirebaseFirestore.getInstance("servedb")}

    // TODO: ADD FUNCTIONS FOR ADDING DATA TO DB FOR
    // TODO: - ACTIVITIES AND VOLUNTEERING EFFORTS


}