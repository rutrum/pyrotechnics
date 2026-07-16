"""Generate block and GUI textures for Pyrotechnics mod."""
import struct, zlib, math, random
from PIL import Image, ImageDraw

random.seed(42)

# === Helpers ===

def wood_grain(size, base, dark, streak_color):
    """Create a simple wood grain texture."""
    img = Image.new('RGBA', (size, size))
    pix = img.load()
    for y in range(size):
        for x in range(size):
            noise = random.randint(-15, 15)
            grain = int(8 * math.sin(y * 0.3 + x * 0.05))
            v = base + noise + grain
            v = max(0, min(255, v))
            pix[x, y] = (v, v - 10, v - 20, 255)
    return img

def add_highlight(img, x, y, w, h, strength=30):
    """Add a top-left highlight to a region."""
    draw = ImageDraw.Draw(img)
    for i in range(h):
        for j in range(w):
            r, g, b, a = img.getpixel((x+j, y+i))
            fade = max(0, 1 - (i+j) / (w+h)) * strength
            img.putpixel((x+j, y+i), (min(255, r+int(fade)), min(255, g+int(fade)), min(255, b+int(fade)), a))

def add_shadow(img, x, y, w, h, strength=30):
    """Add a bottom-right shadow to a region."""
    draw = ImageDraw.Draw(img)
    for i in range(h):
        for j in range(w):
            r, g, b, a = img.getpixel((x+j, y+i))
            fade = max(0, (i+j) / (w+h)) * strength
            img.putpixel((x+j, y+i), (max(0, r-int(fade)), max(0, g-int(fade)), max(0, b-int(fade)), a))

# === Color Vat ===

def make_color_vat_top():
    img = Image.new('RGBA', (16, 16))
    pix = img.load()
    # Wooden rim
    for y in range(16):
        for x in range(16):
            if x < 1 or x >= 15 or y < 1 or y >= 15:
                # Rim
                v = 120 + int(20 * math.sin(y * 0.5 + x * 0.3))
                pix[x, y] = (v, v-10, v-20, 255)
            elif x < 2 or x >= 14 or y < 2 or y >= 14:
                # Inner rim
                v = 100 + int(15 * math.sin(y * 0.7 + x * 0.4))
                pix[x, y] = (v, v-8, v-15, 255)
            else:
                # Liquid - dark purple/red dye
                dx, dy = x-8, y-8
                dist = math.sqrt(dx*dx + dy*dy)
                r = int(80 + 30 * math.sin(dist * 1.5 + x * 0.2))
                g = int(20 + 15 * math.sin(dist * 1.2 + y * 0.3))
                b = int(100 + 40 * math.sin(dist * 1.8 + x * 0.1))
                pix[x, y] = (r, g, b, 255)
    # Highlights on rim
    draw = ImageDraw.Draw(img)
    draw.rectangle([0, 0, 15, 0], fill=(160, 140, 100, 255))
    draw.rectangle([0, 0, 0, 15], fill=(150, 130, 95, 255))
    add_highlight(img, 2, 2, 12, 12, 20)
    return img

def make_color_vat_side():
    img = Image.new('RGBA', (16, 16))
    pix = img.load()
    # Vertical wooden planks
    for x in range(16):
        plank = x // 4
        for y in range(16):
            v = 130 + int(20 * math.sin(y * 0.5 + plank * 2))
            v += random.randint(-8, 8)
            # Horizontal bands (metal rings)
            if y in [3, 4, 12, 13]:
                v = 90 + random.randint(-5, 5)
            pix[x, y] = (v, v-10, v-20, 255)
    # Dye stain on bottom
    for x in range(16):
        for y in range(10, 16):
            r, g, b, a = img.getpixel((x, y))
            stain = (14 - y) * 8
            pix[x, y] = (max(0, r-stain), max(0, g-stain//2), min(255, b+stain//2), 255)
    # Metal ring highlights
    draw = ImageDraw.Draw(img)
    draw.rectangle([0, 3, 15, 3], fill=(110, 105, 95, 255))
    draw.rectangle([0, 12, 15, 12], fill=(110, 105, 95, 255))
    return img

# === Effect Bench ===

def make_effect_bench_top():
    img = Image.new('RGBA', (16, 16))
    pix = img.load()
    # Wooden work surface
    for y in range(16):
        for x in range(16):
            v = 150 + int(25 * math.sin(y * 0.4 + x * 0.3))
            v += random.randint(-5, 5)
            pix[x, y] = (v, v-8, v-18, 255)
    # Darker tool marks / grooves
    draw = ImageDraw.Draw(img)
    # Cross groove
    draw.rectangle([7, 0, 8, 5], fill=(100, 88, 70, 255))
    draw.rectangle([7, 10, 8, 15], fill=(100, 88, 70, 255))
    # Small circular indentations
    for cx, cy in [(2, 2), (13, 2), (2, 13), (13, 13)]:
        draw.ellipse([cx-1, cy-1, cx+1, cy+1], fill=(110, 95, 75, 255))
    # Metal tool rest
    draw.rectangle([3, 6, 12, 9], fill=(130, 115, 95, 255))
    draw.rectangle([4, 7, 11, 8], fill=(100, 90, 75, 255))
    add_highlight(img, 0, 0, 16, 16, 15)
    return img

def make_effect_bench_side():
    img = Image.new('RGBA', (16, 16))
    pix = img.load()
    # Drawer front look
    for y in range(16):
        for x in range(16):
            if 3 <= y <= 12:
                # Drawer
                v = 140 + int(15 * math.sin(y * 0.3 + x * 0.2))
            else:
                # Frame
                v = 120 + int(15 * math.sin(y * 0.5 + x * 0.3))
            v += random.randint(-5, 5)
            pix[x, y] = (v, v-8, v-18, 255)
    # Drawer handle
    draw = ImageDraw.Draw(img)
    draw.rectangle([5, 6, 10, 7], fill=(90, 78, 60, 255))
    draw.rectangle([5, 9, 10, 10], fill=(90, 78, 60, 255))
    add_highlight(img, 0, 0, 16, 16, 10)
    return img

# === Assembly Bench ===

def make_assembly_bench_top():
    img = Image.new('RGBA', (16, 16))
    pix = img.load()
    # Lighter wood table
    for y in range(16):
        for x in range(16):
            v = 160 + int(20 * math.sin(y * 0.3 + x * 0.4))
            v += random.randint(-4, 4)
            pix[x, y] = (v, v-6, v-15, 255)
    # Paper area (lighter rectangle)
    draw = ImageDraw.Draw(img)
    draw.rectangle([3, 2, 12, 7], fill=(220, 210, 190, 255))
    draw.rectangle([4, 3, 11, 6], fill=(240, 230, 210, 255))
    # Gunpowder marks (small dark specks)
    for _ in range(6):
        gx = random.randint(10, 14)
        gy = random.randint(8, 12)
        img.putpixel((gx, gy), (40, 35, 30, 255))
    # Star placement area (circle)
    draw.ellipse([9, 10, 14, 15], fill=(180, 165, 140, 255))
    draw.ellipse([10, 11, 13, 14], fill=(200, 185, 160, 255))
    add_highlight(img, 0, 0, 16, 16, 12)
    return img

def make_assembly_bench_side():
    img = Image.new('RGBA', (16, 16))
    pix = img.load()
    # Simple wood panel with legs
    for y in range(16):
        for x in range(16):
            if x < 2 or x >= 14:
                # Leg area
                v = 130 + int(10 * math.sin(y * 0.4))
            else:
                # Panel
                v = 145 + int(15 * math.sin(y * 0.3 + x * 0.2))
            v += random.randint(-5, 5)
            pix[x, y] = (v, v-8, v-18, 255)
    # Shelf
    draw = ImageDraw.Draw(img)
    draw.rectangle([2, 8, 13, 9], fill=(120, 105, 85, 255))
    add_highlight(img, 0, 0, 16, 16, 10)
    return img

# === GUI Textures ===

def make_gui(width, height, color, draw_fn):
    """Create a GUI texture with a solid background and custom drawing."""
    img = Image.new('RGBA', (width, height))
    # Base background - dark wood
    pix = img.load()
    for y in range(height):
        for x in range(width):
            v = color[0] + random.randint(-8, 8)
            img.putpixel((x, y), (v, v-6, v-12, 255))
    draw_fn(ImageDraw.Draw(img), img)
    return img

def draw_color_vat_gui(draw, img):
    w, h = img.size
    # Slot area backgrounds (lighter insets)
    for row in range(4):
        for col in range(4):
            sx, sy = 26 + col*18, 17 + row*18
            draw.rectangle([sx, sy, sx+15, sy+15], fill=(60, 50, 40, 255))
    # Gunpowder slot
    draw.rectangle([26, 93, 41, 108], fill=(60, 50, 40, 255))
    # Result slot
    draw.rectangle([134, 53, 149, 68], fill=(60, 50, 40, 255))
    # Labels area
    draw.text((62, 78), "Base", fill=(180, 160, 130))
    draw.text((110, 78), "Fade", fill=(180, 160, 130))
    # Player inventory area
    draw.rectangle([7, 124, 168, 206], fill=(50, 42, 35, 255))

def draw_effect_bench_gui(draw, img):
    w, h = img.size
    # Star slot
    draw.rectangle([44, 35, 59, 50], fill=(60, 50, 40, 255))
    # Shape slot
    draw.rectangle([26, 57, 41, 72], fill=(60, 50, 40, 255))
    # Diamond slot
    draw.rectangle([62, 57, 77, 72], fill=(60, 50, 40, 255))
    # Glowstone slot
    draw.rectangle([80, 57, 95, 72], fill=(60, 50, 40, 255))
    # Result slot
    draw.rectangle([134, 35, 149, 50], fill=(60, 50, 40, 255))
    # Player inventory area
    draw.rectangle([7, 83, 168, 165], fill=(50, 42, 35, 255))

def draw_assembly_bench_gui(draw, img):
    w, h = img.size
    # Paper slot
    draw.rectangle([44, 35, 59, 50], fill=(60, 50, 40, 255))
    # Gunpowder slot
    draw.rectangle([62, 35, 77, 50], fill=(60, 50, 40, 255))
    # Star slots (8)
    for i in range(8):
        sx, sy = 98 + (i%4)*18, 17 + (i//4)*18
        draw.rectangle([sx, sy, sx+15, sy+15], fill=(60, 50, 40, 255))
    # Result slot
    draw.rectangle([152, 35, 167, 50], fill=(60, 50, 40, 255))
    # Player inventory area
    draw.rectangle([7, 83, 168, 165], fill=(50, 42, 35, 255))

# === Generate All ===

base = "src/main/resources/assets/pyrotechnics/textures"

textures = {
    f"{base}/block/color_vat_top.png": make_color_vat_top(),
    f"{base}/block/color_vat.png": make_color_vat_side(),
    f"{base}/block/effect_bench_top.png": make_effect_bench_top(),
    f"{base}/block/effect_bench.png": make_effect_bench_side(),
    f"{base}/block/assembly_bench_top.png": make_assembly_bench_top(),
    f"{base}/block/assembly_bench.png": make_assembly_bench_side(),
    f"{base}/gui/color_vat.png": make_gui(176, 207, (100, 85, 65), draw_color_vat_gui),
    f"{base}/gui/effect_bench.png": make_gui(176, 174, (110, 92, 70), draw_effect_bench_gui),
    f"{base}/gui/assembly_bench.png": make_gui(176, 174, (115, 95, 72), draw_assembly_bench_gui),
}

for path, img in textures.items():
    img.save(path)
    print(f"  {path}  ({img.size[0]}x{img.size[1]})")

print("Done!")