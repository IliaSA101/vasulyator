# Промт для иконки Васюлятра

Идея: «10 яблок на троих — одно лишнее» — это и есть остаток.

## Настройки

- Модель: NB2 (запасной вариант — Flux)
- Соотношение сторон: 1:1
- Разрешение: 1024×1024 или больше
- Вариантов: 2–4

## Промт

```
Mobile app icon for a children's calculator. A cute smiling cartoon calculator character with big friendly eyes, its screen shows a large division sign "÷". Next to it, one shiny red apple sits alone, as if "left over". Flat vector illustration, bold clean shapes, thick soft outlines, bright cheerful colors: blue calculator, red apple. Solid plain warm-yellow background (#FFCA28), no gradient, no texture. Subject centered, occupying about 60% of the canvas, generous empty margin around it. No text, no letters, no numbers except the division sign. Simple and readable at small size.
```

## Негативный промт (если есть поле)

```
text, letters, words, watermark, photo, realistic, 3d render, gradient background, busy details, frame, border
```

## Куда положить результат

`design/icon.png` — фон сплошной жёлтый, персонаж по центру с запасом по краям:
Android обрезает иконку кругом/«squircle», края не должны содержать важного.
