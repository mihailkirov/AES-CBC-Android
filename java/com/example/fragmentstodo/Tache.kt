package com.example.fragmentstodo

import java.io.Serializable

class Tache(val nom:String?, val duree:Int?, val description:String?, val categorie:Categorie?, val place:String?=""):
    Serializable{

    enum class Categorie {
        Autre, Travail, Sport, Menage, Lecture, Enfants, Courses

    }

    /**
     * Getters and setters are like python obj.att
     */

}