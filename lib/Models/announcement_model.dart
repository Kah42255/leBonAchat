class AnnouncementModel {
  final int id;
  final String titre;
  final String description;
  final double prix;
  final String ville;
  final String? image;
  final String category;
  final String createdBy;

  AnnouncementModel({
    required this.id,
    required this.titre,
    required this.description,
    required this.prix,
    required this.ville,
    this.image,
    required this.category,
    required this.createdBy,
  });

  factory AnnouncementModel.fromJson(Map<String, dynamic> json) {
    return AnnouncementModel(
      id: json['id'],
      titre: json['titre'],
      description: json['description'],
      prix: (json['prix'] as num).toDouble(),
      ville: json['ville'],
      image: json['imagePath'],       // correspond à backend
      category: json['category'],
      createdBy: json['createdBy'],
    );
  }
}
