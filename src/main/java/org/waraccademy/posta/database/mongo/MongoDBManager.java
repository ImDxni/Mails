package org.waraccademy.posta.database.mongo;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.reactivestreams.client.MongoCollection;
import me.lolok.containers.items.ItemStackSerializer;
import org.bson.Document;
import org.bukkit.inventory.ItemStack;
import org.waraccademy.items.nbt.NBTUtils;
import org.waraccademy.mongo.database.IMongoDBManager;
import org.waraccademy.mongo.database.subscribers.ObservableSubscriber;
import org.waraccademy.posta.Posta;
import org.waraccademy.posta.database.mongo.subscribers.CompletableSubscriber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class MongoDBManager {
    private final IMongoDBManager manager;
    private MongoCollection<Document> collection;
    private final MongoCollection<Document> containers;

    public MongoDBManager(Posta plugin) {
        manager = plugin.getMongo();

        manager.collectionExist("mailboxes").whenComplete((exists, e) -> {
            if (e == null) {
                if (exists) {
                    collection = manager.getCollection("mailboxes");
                } else {
                    collection = manager.createCollectionAndGet("mailboxes");
                    collection.createIndex(Indexes.ascending("id")).subscribe(new ObservableSubscriber<>());
                }
            }
        });

        containers = manager.getCollection("containers");
    }



    public void deleteMailbox(int id){
        collection.deleteOne(Filters.eq("id",id)).subscribe(new ObservableSubscriber<>());
    }

    public void deletePackage(int id){
        containers.deleteOne(Filters.eq("id",id)).subscribe(new ObservableSubscriber<>());
    }

    public void createMailbox(int id){
        saveMailbox(id, Collections.emptyList());
    }

    public void saveMailbox(int id, List<ItemStack> items) {
        Document document = new Document();

        document.append("id", id);

        List<Document> documents = new ArrayList<>();

        for (ItemStack item : items) {
            Document itemDocument = new Document();
            itemDocument.put("item", NBTUtils.toJSON(item));
            documents.add(itemDocument);
        }

        document.put("items", documents);

        collection.replaceOne(Filters.eq("id", id), document, (new ReplaceOptions()).upsert(true)).subscribe(new ObservableSubscriber<>());
    }

    public CompletableFuture<List<ItemStack>> getMailboxItems(int id) {
        CompletableSubscriber<Document, List<ItemStack>> subscriber = new CompletableSubscriber<>(
                (document) -> {
                    List<Document> items = document.getList("items", Document.class);

                    return items.stream().map(ItemStackSerializer::deserialize).collect(Collectors.toList());
                });

        collection.find(Filters.eq("id", id))
                .subscribe(subscriber);

        return subscriber.getResult();
    }
}
