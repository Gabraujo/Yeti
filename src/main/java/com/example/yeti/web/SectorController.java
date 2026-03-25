package com.example.yeti.web;

import com.example.yeti.domain.Item;
import com.example.yeti.domain.Sector;
import com.example.yeti.repository.ItemRepository;
import com.example.yeti.repository.SectorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/setores")
public class SectorController {

    private final SectorRepository sectorRepository;
    private final ItemRepository itemRepository;

    public SectorController(SectorRepository sectorRepository, ItemRepository itemRepository) {
        this.sectorRepository = sectorRepository;
        this.itemRepository = itemRepository;
    }

    @GetMapping
    public List<SectorResponse> listarSetores() {
        return sectorRepository.findAll().stream()
                .map(SectorResponse::from)
                .toList();
    }

    @GetMapping("/{codigo}/itens")
    public List<ItemResponse> itensPorSetor(@PathVariable String codigo) {
        return itemRepository.findBySector_Code(codigo).stream()
                .map(ItemResponse::from)
                .toList();
    }

    @PostMapping("/{codigo}/itens")
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse criarItem(@PathVariable String codigo, @RequestBody SaveItemRequest request) {
        Sector sector = sectorRepository.findByCode(codigo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Setor nao encontrado"));
        Item item = new Item(
                request.code(),
                request.description(),
                request.batches(),
                request.quantityPerBatch(),
                sector
        );
        Item saved = itemRepository.save(item);
        return ItemResponse.from(saved);
    }

    @PutMapping("/{codigo}/itens/{id}")
    public ItemResponse atualizarItem(@PathVariable String codigo, @PathVariable Long id, @RequestBody SaveItemRequest request) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item nao encontrado"));

        if (!item.getSector().getCode().equals(codigo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item nao pertence ao setor informado");
        }

        item.update(request.code(), request.description(), request.batches(), request.quantityPerBatch());
        return ItemResponse.from(itemRepository.save(item));
    }

    @DeleteMapping("/{codigo}/itens/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removerItem(@PathVariable String codigo, @PathVariable Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item nao encontrado"));

        if (!item.getSector().getCode().equals(codigo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Item nao pertence ao setor informado");
        }

        itemRepository.delete(item);
    }

    public record SectorResponse(Long id, String code, String name) {
        static SectorResponse from(Sector sector) {
            return new SectorResponse(sector.getId(), sector.getCode(), sector.getName());
        }
    }

    public record ItemResponse(Long id, String code, String description, int batches, int quantityPerBatch) {
        static ItemResponse from(Item item) {
            return new ItemResponse(
                    item.getId(),
                    item.getCode(),
                    item.getDescription(),
                    item.getBatches(),
                    item.getQuantityPerBatch()
            );
        }
    }

    public record SaveItemRequest(String code, String description, int batches, int quantityPerBatch) { }
}
