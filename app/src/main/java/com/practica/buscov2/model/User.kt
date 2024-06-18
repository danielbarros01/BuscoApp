package com.practica.buscov2.model

data class User(
    val id: Int? = null,
    val name: String? = null,
    val lastname: String? = null,
    val username: String? = null,
    val email: String? = null,
    var birthdate: String? = null,
    val country: String? = null,
    val province: String? = null,
    val department: String? = null,
    val city: String? = null,
    val telephone: String? = null,
    val image: String? = null,
    val verificationCode: Int? = null,
    val confirmed: Boolean? = null,
    val googleId: String? = null,
)

/*
 public int Id { get; set; }
 public string Name { get; set; }
 public string Lastname { get; set; }
 public string Username { get; set; }
 public string Email { get; set; }
 public DateTime Birthdate { get; set; }
 public string Country { get; set; }
 public string Province { get; set; }
 public string Department { get; set; }
 public string City { get; set; }
 public string Telephone { get; set; }
 public string? Image { get; set; }
 public int? VerificationCode { get; set; }
 public bool? Confirmed { get; set; } = false;
 public string? Google_id { get; set; }* */