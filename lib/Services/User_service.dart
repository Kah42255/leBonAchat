import 'dart:convert';
import 'package:http/http.dart' as http;
import '../Models/User.dart';
import '../Models/user_session.dart';

class UserService {
  static const String baseUrl = "http://192.168.1.2:8080/api/auth";

  static Future<bool> register(Map<String, dynamic> data) async {
    try {
      final response = await http.post(
        Uri.parse("$baseUrl/register"),
        headers: {"Content-Type": "application/json"},
        body: jsonEncode(data),
      );
      return response.statusCode == 200 || response.statusCode == 201;
    } catch (e) {
      print("Erreur register: $e");
      return false;
    }
  }

  static Future<UserModel?> login(String email, String password) async {
    try {
      final response = await http.post(
        Uri.parse("$baseUrl/login"),
        headers: {"Content-Type": "application/json"},
        body: jsonEncode({"email": email, "password": password}),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        final userJson = data['user'];
        final token = data['token'];

        UserModel user = UserModel.fromJson(userJson);
        await UserSession.login(user, token);

        return user;
      } else {
        return null;
      }
    } catch (e) {
      print("Erreur login: $e");
      return null;
    }
  }
}
