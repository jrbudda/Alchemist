package net.apunch.alchemist;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class PotionRecipe {
    private final String name;
    private final boolean splash;
    private PotionEffect effect;
    public Set<ItemStack> ingredients;

    public PotionRecipe(String name, PotionEffectType type, int duration, int amplifier, boolean splash,
            ItemStack... required) {
        this.name = name;
        this.splash = splash;
        effect = new PotionEffect(type, duration, amplifier);
        ingredients = new HashSet<ItemStack>(Arrays.asList(required));
    }

    public String getName() {
        return name;
    }

    public ItemStack getResult() {
        Potion potion = new Potion(PotionType.getByEffect(effect.getType()));
        potion.setSplash(splash);
        return potion.toItemStack(1);
    }

    public boolean hasIngredient(ItemStack ingredient) {
        for (ItemStack stack : ingredients)
            if (stack != null && stack.getTypeId() == ingredient.getTypeId()
                    && stack.getDurability() == ingredient.getDurability()
                    && (stack.getEnchantments().equals(ingredient.getEnchantments())))
                return true;
        return false;
    }

    // Returns whether the player had enough of the item
    public boolean removeIngredientFromHand(Player player) {
        ItemStack hand = player.getItemInHand();
        // Try to remove entire stack
        if (ingredients.contains(hand)) {
            ingredients.remove(hand);
            player.setItemInHand(null);
            return true;
        }
        for (ItemStack ingredient : ingredients) {
            if (ingredient != null && ingredient.getTypeId() == hand.getTypeId()
                    && ingredient.getDurability() == hand.getDurability()
                    && ingredient.getEnchantments().equals(hand.getEnchantments())) {
                ingredients.remove(ingredient);
                if (hand.getAmount() - ingredient.getAmount() < 0) {
                    hand.setAmount(ingredient.getAmount() - hand.getAmount());
                    ingredients.add(hand);
                    player.setItemInHand(null);
                    return false;
                }
                hand.setAmount(hand.getAmount() - ingredient.getAmount() > 0 ? hand.getAmount()
                        - ingredient.getAmount() : 0);
                player.setItemInHand(hand);
                return true;
            }
        }
        return false;
    }

    public boolean isComplete() {
        return ingredients.size() == 1;
    }
}