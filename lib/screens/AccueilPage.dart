import 'package:flutter/material.dart';
import '../Models/announcement_model.dart';
import '../Services/announcement_service.dart';
import '../Models/user_session.dart';
import 'AnnouncementDetailsScreen.dart';

class AccueilScreen extends StatefulWidget {
  const AccueilScreen({super.key});

  @override
  State<AccueilScreen> createState() => _AccueilScreenState();
}

class _AccueilScreenState extends State<AccueilScreen> {
  List<AnnouncementModel> _annonces = [];
  bool _loading = true;
  dynamic _user;
  int? _selectedCategoryId;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    try {
      final user = await UserSession.getUser();

      List<AnnouncementModel> annonces;

      if (_selectedCategoryId != null) {
        annonces = await AnnouncementService.getByCategory(_selectedCategoryId!);
      } else {
        annonces = await AnnouncementService.getAll();
      }

      if (!mounted) return;

      setState(() {
        _user = user;
        _annonces = annonces;
        _loading = false;
      });
    } catch (e) {
      if (!mounted) return;
      setState(() {
        _loading = false;
      });
      print("Erreur chargement annonces: $e");
    }
  }

  void _filterByCategory(int? categoryId) {
    setState(() {
      _selectedCategoryId = categoryId;
      _loading = true;
    });

    _loadData();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Accueil"),
        backgroundColor: const Color(0xFFE91E63),
      ),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : Column(
        children: [
          // ----------- Catégories -----------
          SingleChildScrollView(
            scrollDirection: Axis.horizontal,
            child: Row(
              children: [
                TextButton(
                  onPressed: () => _filterByCategory(null),
                  child: const Text("Toutes"),
                ),
                TextButton(
                  onPressed: () => _filterByCategory(1),
                  child: const Text("Catégorie 1"),
                ),
                TextButton(
                  onPressed: () => _filterByCategory(2),
                  child: const Text("Catégorie 2"),
                ),
              ],
            ),
          ),

          // ----------- Liste annonces -----------
          Expanded(
            child: _annonces.isEmpty
                ? const Center(child: Text("Aucune annonce disponible"))
                : ListView.builder(
              itemCount: _annonces.length,
              itemBuilder: (context, index) {
                final annonce = _annonces[index];

                return Card(
                  margin: const EdgeInsets.symmetric(
                      horizontal: 12, vertical: 6),
                  child: ListTile(
                    leading: (annonce.image != null &&
                        annonce.image!.isNotEmpty)
                        ? Image.network(
                      annonce.image!,
                      width: 60,
                      height: 60,
                      fit: BoxFit.cover,
                    )
                        : const Icon(Icons.image),

                    title: Text(
                      annonce.titre,
                      style: const TextStyle(
                          fontWeight: FontWeight.bold),
                    ),
                    subtitle: Text(
                        "${annonce.prix} DA - ${annonce.ville}"),

                    onTap: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (_) =>
                              AnnouncementDetailsScreen(
                                  annonce: annonce),
                        ),
                      );
                    },

                    trailing: ElevatedButton(
                      onPressed:
                      _user != null ? () {} : null,
                      style: ElevatedButton.styleFrom(
                        backgroundColor:
                        const Color(0xFFE91E63),
                      ),
                      child: const Text("Contacter"),
                    ),
                  ),
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}
