package com.fyr.finapp.domain.model.category;

import java.util.List;

public record CategoryTemplate(
        String key,
        String name,
        String type,
        String color,
        String icon
) {
    public static List<CategoryTemplate> all() {
        return List.of(
                new CategoryTemplate("SALARIO", "Salario", "INCOME", "#10b981", "💰"),
                new CategoryTemplate("FREELANCE", "Freelance", "INCOME", "#3b82f6", "💻"),
                new CategoryTemplate("ALIMENTACION", "Alimentación", "EXPENSE", "#ef4444", "🍽️"),
                new CategoryTemplate("TRANSPORTE", "Transporte", "EXPENSE", "#f59e0b", "🚗"),
                new CategoryTemplate("VIVIENDA", "Vivienda", "EXPENSE", "#06b6d4", "🏠"),
                new CategoryTemplate("MASCOTAS", "Mascotas", "EXPENSE", "#8b5cf6", "🐾"),
                new CategoryTemplate("ENTRETENIMIENTO", "Entretenimiento", "EXPENSE", "#ec4899", "🎮"),
                new CategoryTemplate("SALUD", "Salud", "EXPENSE", "#3d4451", "❤️"),
                new CategoryTemplate("EDUCACION", "Educación", "EXPENSE", "#14b8a6", "📚"),
                new CategoryTemplate("ROPA", "Ropa", "EXPENSE", "#f97316", "👕"),
                new CategoryTemplate("VIAJES", "Viajes", "EXPENSE", "#0ea5e9", "✈️"),
                new CategoryTemplate("REGALOS", "Regalos", "EXPENSE", "#db2777", "🎁")
        );
    }
}
