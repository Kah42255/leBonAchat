class UserModel {
  final int id;
  final String nom;
  final String prenom;
  final String email;
  final String numTel;
  final String role;

  UserModel({
    required this.id,
    required this.nom,
    required this.prenom,
    required this.email,
    required this.numTel,
    required this.role,
  });

  factory UserModel.fromJson(Map<String, dynamic> json) {
    return UserModel(
      id: json['id'],
      nom: json['nom'],
      prenom: json['prenom'],
      email: json['email'],
      numTel: json['numTel'] ?? '',
      role: json['role'] ?? 'USER',
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'nom': nom,
      'prenom': prenom,
      'email': email,
      'numTel': numTel,
      'role': role,
    };
  }
}
