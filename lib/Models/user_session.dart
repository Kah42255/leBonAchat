import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';
import 'User.dart';

class UserSession {
  static const String _tokenKey = "token";
  static const String _userKey = "user";

  static Future<void> login(UserModel user, String token) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(_tokenKey, token);
    await prefs.setString(_userKey, jsonEncode(user.toJson()));
  }

  static Future<void> logout() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(_tokenKey);
    await prefs.remove(_userKey);
  }

  static Future<bool> hasToken() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.containsKey(_tokenKey);
  }

  static Future<UserModel?> getUser() async {
    final prefs = await SharedPreferences.getInstance();
    final jsonStr = prefs.getString(_userKey);
    if (jsonStr == null) return null;
    final Map<String, dynamic> data = json.decode(jsonStr);
    return UserModel.fromJson(data);
  }

  static Future<String?> getToken() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString(_tokenKey);
  }
}
