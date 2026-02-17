import 'package:flutter/material.dart';
import '../Services/User_service.dart';
import '../Models/user_session.dart';
import 'AccueilPage.dart';
import 'register-screen.dart';

const Color primaryPink = Color(0xFFE91E63);
const Color lightGrey = Color(0xFFF5F5F5);

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _passwordController = TextEditingController();
  bool _loading = false;

  void _login() async {
    setState(() => _loading = true);
    final user = await UserService.login(_emailController.text, _passwordController.text);
    if (user != null) {
      if (!mounted) return;
      Navigator.pushReplacement(context, MaterialPageRoute(builder: (_) => const AccueilScreen()));
    } else {
      setState(() => _loading = false);
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text("Email ou mot de passe incorrect")));
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: lightGrey,
      body: Center(
        child: SingleChildScrollView(
          child: Container(
            margin: const EdgeInsets.all(20),
            padding: const EdgeInsets.all(20),
            decoration: BoxDecoration(color: Colors.white, borderRadius: BorderRadius.circular(20)),
            child: Column(
              children: [
                const Text("lebonachat", style: TextStyle(color: primaryPink, fontSize: 28, fontWeight: FontWeight.bold)),
                const SizedBox(height: 30),
                TextField(controller: _emailController, decoration: InputDecoration(hintText: "Email", border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)))),
                const SizedBox(height: 15),
                TextField(controller: _passwordController, obscureText: true, decoration: InputDecoration(hintText: "Mot de passe", border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)))),
                const SizedBox(height: 25),
                SizedBox(
                  width: double.infinity,
                  height: 45,
                  child: ElevatedButton(
                    onPressed: _loading ? null : _login,
                    style: ElevatedButton.styleFrom(backgroundColor: primaryPink, shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12))),
                    child: _loading ? const CircularProgressIndicator(color: Colors.white) : const Text("Se connecter"),
                  ),
                ),
                TextButton(
                  onPressed: () => Navigator.push(context, MaterialPageRoute(builder: (_) => const RegisterScreen())),
                  child: const Text("Créer un compte"),
                )
              ],
            ),
          ),
        ),
      ),
    );
  }
}
