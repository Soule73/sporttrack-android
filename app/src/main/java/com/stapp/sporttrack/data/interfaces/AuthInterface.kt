package com.stapp.sporttrack.data.interfaces

interface HasEmail {
    val email: String
}

interface HasPassword {
    val password: String
}

interface HasName {
    val firstName: String
    val lastName: String
}

interface HasBirthDate {
    val birthDate: String?
}

interface HasGender {
    val gender: String?
}

interface HasWeight {
    val weight: Double?
}

interface HasHeight {
    val height: Double?
}
