import 'package:flutter/material.dart';
import '../Models/announcement_model.dart';

const Color primaryPink = Color(0xFFE91E63);

class AnnouncementDetailsScreen extends StatelessWidget {
  final AnnouncementModel annonce;
  const AnnouncementDetailsScreen({super.key, required this.annonce});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text(annonce.titre), backgroundColor: primaryPink),
      body: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            if (annonce.image != null && annonce.image!.isNotEmpty)
              Image.network(
                annonce.image!,
                width: double.infinity,
                height: 250,
                fit: BoxFit.cover,
              ),
            Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    annonce.titre,
                    style: const TextStyle(
                        fontSize: 24,
                        fontWeight: FontWeight.bold,
                        color: primaryPink),
                  ),
                  const SizedBox(height: 8),
                  Text("${annonce.prix} DA",
                      style: const TextStyle(
                          fontSize: 20, fontWeight: FontWeight.bold)),
                  const SizedBox(height: 8),
                  Row(children: [
                    Text("Ville: ${annonce.ville}"),
                    const SizedBox(width: 20),
                    Text("Catégorie: ${annonce.category}")
                  ]),
                  const SizedBox(height: 16),
                  const Text("Description:",
                      style:
                      TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                  const SizedBox(height: 8),
                  Text(annonce.description),
                  const SizedBox(height: 16),
                  Text("Publié par: ${annonce.createdBy}",
                      style: const TextStyle(fontStyle: FontStyle.italic)),
                  const SizedBox(height: 20),
                  SizedBox(
                    width: double.infinity,
                    height: 50,
                    child: ElevatedButton(
                      onPressed: () {},
                      style: ElevatedButton.styleFrom(
                        backgroundColor: primaryPink,
                        shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(14)),
                      ),
                      child: const Text("Contacter le vendeur"),
                    ),
                  )
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
