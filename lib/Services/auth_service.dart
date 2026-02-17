import 'dart:convert';
import 'package:http/http.dart' as http;
import '../Models/User.dart';

class AuthService {
  static const String baseUrl = "http://192.168.1.2:8080/api/auth";


  static Future<Map<String, dynamic>?> login(String email, String password) async {
    final response = await http.post(
      Uri.parse("$baseUrl/api/auth/login"),
      headers: {"Content-Type": "application/json"},
      body: jsonEncode({"email": email, "password": password}),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      final user = UserModel.fromJson(data['user']);
      final token = data['token'];
      return {'user': user, 'token': token};
    } else {
      return null;
    }
  }


  static Future<bool> register(Map<String, dynamic> userData) async {
    final response = await http.post(
      Uri.parse("$baseUrl/api/auth/register"),
      headers: {"Content-Type": "application/json"},
      body: jsonEncode(userData),
    );

    return response.statusCode == 200;
  }
}
