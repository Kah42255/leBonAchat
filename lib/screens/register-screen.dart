import 'package:flutter/material.dart';
import '../Services/User_service.dart';
import 'login_screen.dart';

const Color primaryPink = Color(0xFFE91E63);
const Color lightGrey = Color(0xFFF5F5F5);

class RegisterScreen extends StatefulWidget {
  const RegisterScreen({super.key});

  @override
  State<RegisterScreen> createState() => _RegisterScreenState();
}

class _RegisterScreenState extends State<RegisterScreen> {
  final TextEditingController _nom = TextEditingController();
  final TextEditingController _prenom = TextEditingController();
  final TextEditingController _email = TextEditingController();
  final TextEditingController _password = TextEditingController();
  bool _loading = false;

  void _register() async {
    setState(() => _loading = true);
    final data = {
      "nom": _nom.text,
      "prenom": _prenom.text,
      "email": _email.text,
      "password": _password.text,
      "numTel": "0555555555", // par défaut si vide
      "role": "USER",
    };
    final success = await UserService.register(data);
    setState(() => _loading = false);
    if (success) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text("Inscription réussie !")));
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(builder: (_) => const LoginScreen()),
      );
    } else {
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text("Erreur lors de l'inscription")));
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
                const Text("Register", style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold)),
                const SizedBox(height: 20),
                TextField(controller: _nom, decoration: InputDecoration(hintText: "Nom", border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)))),
                const SizedBox(height: 15),
                TextField(controller: _prenom, decoration: InputDecoration(hintText: "Prénom", border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)))),
                const SizedBox(height: 15),
                TextField(controller: _email, decoration: InputDecoration(hintText: "Email", border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)))),
                const SizedBox(height: 15),
                TextField(controller: _password, obscureText: true, decoration: InputDecoration(hintText: "Mot de passe", border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)))),
                const SizedBox(height: 25),
                SizedBox(
                  width: double.infinity,
                  height: 45,
                  child: ElevatedButton(
                    onPressed: _loading ? null : _register,
                    style: ElevatedButton.styleFrom(
                        backgroundColor: primaryPink,
                        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12))),
                    child: _loading
                        ? const CircularProgressIndicator(color: Colors.white)
                        : const Text("Créer un compte"),
                  ),
                ),
                TextButton(
                  onPressed: () => Navigator.pushReplacement(
                    context,
                    MaterialPageRoute(builder: (_) => const LoginScreen()),
                  ),
                  child: const Text("Se connecter"),
                )
              ],
            ),
          ),
        ),
      ),
    );
  }
}
