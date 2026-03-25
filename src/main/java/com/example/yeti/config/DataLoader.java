package com.example.yeti.config;

import com.example.yeti.domain.Item;
import com.example.yeti.domain.Sector;
import com.example.yeti.repository.ItemRepository;
import com.example.yeti.repository.SectorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner seedDatabase(SectorRepository sectorRepository, ItemRepository itemRepository) {
        return args -> {
            if (sectorRepository.count() > 0) {
                return;
            }

            Sector linhaPurificador = sectorRepository.save(new Sector("linha-purificador", "Linha Purificador"));
            Sector preMontagem = sectorRepository.save(new Sector("pre-montagem", "Pre-montagem"));
            Sector compressor = sectorRepository.save(new Sector("compressor", "Compressor"));
            Sector indef = sectorRepository.save(new Sector("indef", "Indef"));

            itemRepository.save(new Item("LP-100", "Filtro principal", 0, 10, linhaPurificador));
            itemRepository.save(new Item("LP-210", "Mangueira 1/2", 0, 10, linhaPurificador));

            itemRepository.save(new Item("PM-301", "Base plastica", 0, 10, preMontagem));

            itemRepository.save(new Item("CP-410", "O-ring", 0, 10, compressor));

            itemRepository.save(new Item("ID-001", "Diversos", 0, 10, indef));
        };
    }
}
