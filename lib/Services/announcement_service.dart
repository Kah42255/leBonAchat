import 'dart:convert';
import 'package:http/http.dart' as http;
import '../Models/announcement_model.dart';

class AnnouncementService {


  static const String baseUrl = "http://localhost:8080/api";


  static Future<List<AnnouncementModel>> getAll() async {
    final response = await http.get(Uri.parse("$baseUrl/annonces"));

    if (response.statusCode == 200) {
      List<dynamic> data = jsonDecode(response.body);
      return data
          .map((json) => AnnouncementModel.fromJson(json))
          .toList();
    } else {
      throw Exception("Erreur récupération annonces");
    }
  }


  static Future<List<AnnouncementModel>> getByCategory(int categoryId) async {
    final response = await http.get(
        Uri.parse("$baseUrl/filter/category?categoryId=$categoryId"));

    if (response.statusCode == 200) {
      List<dynamic> data = jsonDecode(response.body);
      return data
          .map((json) => AnnouncementModel.fromJson(json))
          .toList();
    } else {
      throw Exception("Erreur récupération par catégorie");
    }
  }


  static Future<List<AnnouncementModel>> search(String keyword) async {
    final response = await http.get(
        Uri.parse("$baseUrl/search?keyword=$keyword"));

    if (response.statusCode == 200) {
      List<dynamic> data = jsonDecode(response.body);
      return data
          .map((json) => AnnouncementModel.fromJson(json))
          .toList();
    } else {
      throw Exception("Erreur recherche");
    }
  }
}
