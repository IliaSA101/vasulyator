# Промт для иконки Васюлятра

Аудитория — ребёнок 10 лет, поэтому без мультяшности: минималистичная иконка
в духе современных приложений. Крупный знак «÷», нижняя точка — оранжевая:
это намёк на «остаток».

## Настройки

- Модель: Google Nano Banana 2 (запасной вариант — Flux)
- Соотношение сторон: 1:1
- Качество: 2K, если доступно (иначе 1K)
- Вариантов: 4

## Промт A — мягкий объём (основной)

```
Modern minimalist mobile app icon for a math calculator app. A single bold geometric division sign "÷" in the center: a thick horizontal bar with rounded ends, one round dot above it and one round dot below it. The bar and the top dot are clean white with a subtle soft 3D bevel and a gentle drop shadow. The bottom dot is vivid orange (#FF8F00) with a soft glow, it clearly stands out as the "remainder". Solid flat deep indigo background (#283593) filling the entire canvas edge to edge, no gradient, no texture, no vignette. The sign is centered and occupies about 50% of the canvas, with generous empty margin. Premium, clean, confident style like a top App Store or Material You icon. No text, no letters, no numbers, no border, no rounded-square frame.
```

## Промт B — плоский (если A выйдет слишком «пластиковым»)

```
Flat minimalist mobile app icon for a math calculator app. A single bold geometric division sign "÷" centered: a thick horizontal bar with rounded ends, one round dot above and one round dot below. Bar and top dot are pure white, the bottom dot is vivid orange (#FF8F00) to represent the remainder. Solid flat deep indigo background (#283593) filling the whole canvas edge to edge. Perfect geometry, crisp edges, no shadows, no gradient, no texture. The sign occupies about 50% of the canvas, centered, generous empty margin. No text, no letters, no numbers, no border, no rounded-square frame.
```

## Негативный промт (если есть поле)

```
cartoon, character, face, eyes, apple, mascot, text, letters, numbers, watermark, signature, frame, border, rounded square, app icon mockup, phone mockup, gradient background, vignette, noise, busy details, photo, realistic
```

## Что важно в результате

- Фон должен заполнять **весь квадрат**, без нарисованной рамки или «скруглённого квадрата»:
  Android сам обрежет иконку под форму лаунчера (круг, squircle).
- Знак — строго по центру, с запасом по краям: в безопасную зону попадают
  только центральные ~60% картинки.
