import 'package:flutter/material.dart';
import '../Models/announcement_model.dart';
import '../Services/announcement_service.dart';
import 'AnnouncementDetailsScreen.dart';

class AnnouncementListScreen extends StatefulWidget {
  const AnnouncementListScreen({super.key});

  @override
  State<AnnouncementListScreen> createState() => _AnnouncementListScreenState();
}

class _AnnouncementListScreenState extends State<AnnouncementListScreen> {
  late Future<List<AnnouncementModel>> _future;

  @override
  void initState() {
    super.initState();
    _future = AnnouncementService.getAll();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Annonces")),
      body: FutureBuilder<List<AnnouncementModel>>(
        future: _future,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          } else if (snapshot.hasError) {
            return Center(child: Text("Erreur: ${snapshot.error}"));
          } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
            return const Center(child: Text("Aucune annonce disponible"));
          }

          final annonces = snapshot.data!;
          return ListView.builder(
            itemCount: annonces.length,
            itemBuilder: (context, index) {
              final a = annonces[index];
              return ListTile(
                leading: a.image != null && a.image!.isNotEmpty
                    ? Image.network(a.image!, width: 50, height: 50, fit: BoxFit.cover)
                    : null,
                title: Text(a.titre),
                subtitle: Text("${a.prix} DA - ${a.ville}"),
                onTap: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (_) => AnnouncementDetailsScreen(annonce: a),
                    ),
                  );
                },
              );
            },
          );
        },
      ),
    );
  }
}
