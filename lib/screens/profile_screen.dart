import 'package:flutter/material.dart';
import '../Models/user_session.dart';
import 'login_screen.dart';

class ProfilScreen extends StatefulWidget {
  const ProfilScreen({super.key});

  @override
  State<ProfilScreen> createState() => _ProfilScreenState();
}

class _ProfilScreenState extends State<ProfilScreen> {
  var user;

  @override
  void initState() {
    super.initState();
    _loadProfile();
  }

  void _loadProfile() async {
    final u = await UserSession.getUser();
    setState(() => user = u);
  }

  void _logout() async {
    await UserSession.logout();
    if (!mounted) return;
    Navigator.pushReplacement(
        context, MaterialPageRoute(builder: (_) => const LoginScreen()));
  }

  @override
  Widget build(BuildContext context) {
    if (user == null) {
      return const Scaffold(
          body: Center(child: CircularProgressIndicator()));
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text("Profil"),
        backgroundColor: primaryPink,
        actions: [IconButton(icon: const Icon(Icons.logout), onPressed: _logout)],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text("Nom : ${user.nom}"),
            Text("Prénom : ${user.prenom}"),
            Text("Email : ${user.email}"),
            Text("Téléphone : ${user.numTel}"),
            Text("Rôle : ${user.role}"),
          ],
        ),
      ),
    );
  }
}
