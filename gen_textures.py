"""Generate block and GUI textures for Pyrotechnics Minecraft mod.

Usage: uv run python3 gen_textures.py

Scene description language:
  Each GUI is described as a dict with "size" and "elements".
  A render() function processes the scene and produces a PNG.

Primitives:
  - window       — fills bg with grey, draws outer bevel border
  - slot         — 18×18 sunken slot at (x, y)
  - output_slot  — 26×26 larger output slot at (x, y)
  - slot_grid    — grid of slots (cols×rows, dx/dy spacing)
  - arrow        — furnace-style progress arrow at (x, y)
  - label        — text at (x, y)
  - player_inv   — 3×9 + 1×9 hotbar slots starting at y_offset
"""

from PIL import Image, ImageDraw
import math, random, os

random.seed(42)

# ── Vanilla colour palette ────────────────────────────────────────
# Measured from assets/minecraft/textures/gui/container/crafting_table.png
C = {
    "bg":           (198, 198, 198),   # container background fill
    "outer_light":  (255, 255, 255),   # top/left border bevel
    "outer_dark":   ( 85,  85,  85),   # bottom/right border bevel
    "slot_bg":      (139, 139, 139),   # dark slot interior
    "inner_border": ( 55,  55,  55),   # band between border and slots
    "slot_light":   (255, 255, 255),   # slot bevel top/left (sunken)
    "slot_dark":    ( 55,  55,  55),   # slot bevel bottom/right
}


# ── Drawing helpers ────────────────────────────────────────────────

def _hspan(img, x, y, w, color):
    """Draw a horizontal 1‑px span."""
    for dx in range(w):
        img.putpixel((x + dx, y), color)


def _vspan(img, x, y, h, color):
    """Draw a vertical 1‑px span."""
    for dy in range(h):
        img.putpixel((x, y + dy), color)


# ── Bevel box ──────────────────────────────────────────────────────

def bevel_box(draw, x, y, w, h, top_left, bottom_right, fill=None):
    """Draw a beveled rectangle (1‑px border, TL lighter, BR darker)."""
    # Fill the interior (inside the border)
    if fill:
        draw.rectangle([x + 1, y + 1, x + w - 2, y + h - 2], fill=fill)

    # Draw full border rectangle (all 4 edges)
    draw.rectangle([x, y, x + w - 1, y + h - 1], outline=top_left)
    # Overwrite bottom and right edges with darker color
    draw.line([(x, y + h - 1), (x + w - 1, y + h - 1)], fill=bottom_right, width=1)  # bottom
    draw.line([(x + w - 1, y), (x + w - 1, y + h - 1)], fill=bottom_right, width=1)  # right
    # Also need to fix bottom-left and top-right corners - draw them separately
    draw.im.putpixel((x, y + h - 1), bottom_right)  # bottom-left corner
    draw.im.putpixel((x + w - 1, y), bottom_right)  # top-right corner


# ── Primitives ─────────────────────────────────────────────────────

def window(draw, img, w, h):
    """Fill background and draw outer container border."""
    # Fill entire area with bg color
    draw.rectangle([0, 0, w - 1, h - 1], fill=C["bg"])
    # Draw outer bevel border (1px all around)
    bevel_box(draw, 0, 0, w, h, C["outer_light"], C["outer_dark"])
    # Inner border band (1px outline inside the outer border)
    draw.rectangle([1, 1, w - 2, h - 2], outline=C["inner_border"])


def slot(draw, img, x, y, w=18, h=18):
    """Draw one sunken slot at (x, y) with size wxh."""

    draw.rectangle([x, y, x + w - 1, y + h - 1], fill=C["slot_bg"]) # corners
    draw.rectangle([x, y, x + w - 2, y + h - 2], fill=C["slot_dark"])
    draw.rectangle([x + 1, y + 1, x + w - 1, y + h - 1], fill=C["slot_light"])
    draw.rectangle([x + 1, y+1, x + w - 2, y + h - 2], fill=C["slot_bg"])


def output_slot(draw, img, x, y):
    """Draw a larger output slot (like furnace output)."""
    slot(draw, img, x, y, w=26, h=26)


def slot_grid(draw, img, x, y, cols, rows, dx=18, dy=18):
    """Draw a grid of regular slots."""
    for row in range(rows):
        for col in range(cols):
            slot(draw, img, x + col * dx, y + row * dy)


def arrow(draw, img, x, y, filled=False):
    """Draw a furnace‑style fuel/arrow indicator.

    Unfilled = empty arrow outline.
    Filled   = solid arrow (for progress overlay).
    """
    # Arrow shape: 23×16 px bounding box
    # Points: (0,4) → (14,4) → (14,0) → (22,8) → (14,16) → (14,12) → (0,12)
    pts = [(0, 4), (14, 4), (14, 0), (22, 8), (14, 16), (14, 12), (0, 12)]
    pts = [(x + px, y + py) for px, py in pts]
    if filled:
        draw.polygon(pts, fill=C["slot_bg"])
    else:
        draw.polygon(pts, outline=C["outer_dark"])


def label(draw, img, x, y, text, color=(64, 64, 64)):
    """Draw a text label."""
    draw.text((x, y), text, fill=color)


def player_inventory(draw, img, y_offset):
    """Draw 3 rows of 9 slots + hotbar row at the given y offset."""
    inv_x = 8  # matches Java slot positions
    slot_grid(draw, img, inv_x, y_offset, cols=9, rows=3)
    hotbar_y = y_offset + 3 * 18 + 4
    # hotbar has a slightly different background
    draw.rectangle([inv_x - 1, hotbar_y - 1, inv_x + 9 * 18 - 1, hotbar_y + 18 - 1],
                   fill=C["inner_border"])
    slot_grid(draw, img, inv_x, hotbar_y, cols=9, rows=1)


# ── Render ─────────────────────────────────────────────────────────

def render(scene):
    """Produce a PIL Image from a scene description dict."""
    w, h = scene["size"]
    img = Image.new("RGBA", (w, h))
    draw = ImageDraw.Draw(img)
    for el in scene.get("elements", []):
        t = el["type"]
        if t == "window":
            window(draw, img, w, h)
        elif t == "slot":
            slot(draw, img, el["x"], el["y"])
        elif t == "output_slot":
            output_slot(draw, img, el["x"], el["y"])
        elif t == "slot_grid":
            slot_grid(draw, img, el["x"], el["y"], el["cols"], el["rows"],
                      el.get("dx", 18), el.get("dy", 18))
        elif t == "arrow":
            arrow(draw, img, el["x"], el["y"], el.get("filled", False))
        elif t == "label":
            label(draw, img, el["x"], el["y"], el["text"],
                  el.get("color", (64, 64, 64)))
        elif t == "player_inv":
            player_inventory(draw, img, el["y"])
        else:
            raise ValueError(f"Unknown element type: {t}")
    return img


# ── Block textures (keep existing procedural ones) ────────────────

def make_color_vat_top():
    img = Image.new("RGBA", (16, 16))
    pix = img.load()
    for y in range(16):
        for x in range(16):
            if x < 1 or x >= 15 or y < 1 or y >= 15:
                v = 120 + int(20 * math.sin(y * 0.5 + x * 0.3))
                pix[x, y] = (v, v - 10, v - 20, 255)
            elif x < 2 or x >= 14 or y < 2 or y >= 14:
                v = 100 + int(15 * math.sin(y * 0.7 + x * 0.4))
                pix[x, y] = (v, v - 8, v - 15, 255)
            else:
                dx, dy = x - 8, y - 8
                dist = math.sqrt(dx * dx + dy * dy)
                r = int(80 + 30 * math.sin(dist * 1.5 + x * 0.2))
                g = int(20 + 15 * math.sin(dist * 1.2 + y * 0.3))
                b = int(100 + 40 * math.sin(dist * 1.8 + x * 0.1))
                pix[x, y] = (r, g, b, 255)
    draw = ImageDraw.Draw(img)
    draw.rectangle([0, 0, 15, 0], fill=(160, 140, 100, 255))
    draw.rectangle([0, 0, 0, 15], fill=(150, 130, 95, 255))
    return img


def make_color_vat_side():
    img = Image.new("RGBA", (16, 16))
    pix = img.load()
    for x in range(16):
        plank = x // 4
        for y in range(16):
            v = 130 + int(20 * math.sin(y * 0.5 + plank * 2))
            v += random.randint(-8, 8)
            if y in (3, 4, 12, 13):
                v = 90 + random.randint(-5, 5)
            pix[x, y] = (v, v - 10, v - 20, 255)
    for x in range(16):
        for y in range(10, 16):
            r, g, b, a = img.getpixel((x, y))
            stain = (14 - y) * 8
            pix[x, y] = (max(0, r - stain), max(0, g - stain // 2),
                         min(255, b + stain // 2), 255)
    draw = ImageDraw.Draw(img)
    draw.rectangle([0, 3, 15, 3], fill=(110, 105, 95, 255))
    draw.rectangle([0, 12, 15, 12], fill=(110, 105, 95, 255))
    return img


def make_effect_bench_top():
    img = Image.new("RGBA", (16, 16))
    pix = img.load()
    for y in range(16):
        for x in range(16):
            v = 150 + int(25 * math.sin(y * 0.4 + x * 0.3))
            v += random.randint(-5, 5)
            pix[x, y] = (v, v - 8, v - 18, 255)
    draw = ImageDraw.Draw(img)
    draw.rectangle([7, 0, 8, 5], fill=(100, 88, 70, 255))
    draw.rectangle([7, 10, 8, 15], fill=(100, 88, 70, 255))
    for cx, cy in ((2, 2), (13, 2), (2, 13), (13, 13)):
        draw.ellipse([cx - 1, cy - 1, cx + 1, cy + 1], fill=(110, 95, 75, 255))
    draw.rectangle([3, 6, 12, 9], fill=(130, 115, 95, 255))
    draw.rectangle([4, 7, 11, 8], fill=(100, 90, 75, 255))
    return img


def make_effect_bench_side():
    img = Image.new("RGBA", (16, 16))
    pix = img.load()
    for y in range(16):
        for x in range(16):
            if 3 <= y <= 12:
                v = 140 + int(15 * math.sin(y * 0.3 + x * 0.2))
            else:
                v = 120 + int(15 * math.sin(y * 0.5 + x * 0.3))
            v += random.randint(-5, 5)
            pix[x, y] = (v, v - 8, v - 18, 255)
    draw = ImageDraw.Draw(img)
    draw.rectangle([5, 6, 10, 7], fill=(90, 78, 60, 255))
    draw.rectangle([5, 9, 10, 10], fill=(90, 78, 60, 255))
    return img


def make_assembly_bench_top():
    img = Image.new("RGBA", (16, 16))
    pix = img.load()
    for y in range(16):
        for x in range(16):
            v = 160 + int(20 * math.sin(y * 0.3 + x * 0.4))
            v += random.randint(-4, 4)
            pix[x, y] = (v, v - 6, v - 15, 255)
    draw = ImageDraw.Draw(img)
    draw.rectangle([3, 2, 12, 7], fill=(220, 210, 190, 255))
    draw.rectangle([4, 3, 11, 6], fill=(240, 230, 210, 255))
    for _ in range(6):
        gx, gy = random.randint(10, 14), random.randint(8, 12)
        img.putpixel((gx, gy), (40, 35, 30, 255))
    draw.ellipse([9, 10, 14, 15], fill=(180, 165, 140, 255))
    draw.ellipse([10, 11, 13, 14], fill=(200, 185, 160, 255))
    return img


def make_assembly_bench_side():
    img = Image.new("RGBA", (16, 16))
    pix = img.load()
    for y in range(16):
        for x in range(16):
            if x < 2 or x >= 14:
                v = 130 + int(10 * math.sin(y * 0.4))
            else:
                v = 145 + int(15 * math.sin(y * 0.3 + x * 0.2))
            v += random.randint(-5, 5)
            pix[x, y] = (v, v - 8, v - 18, 255)
    draw = ImageDraw.Draw(img)
    draw.rectangle([2, 8, 13, 9], fill=(120, 105, 85, 255))
    return img


# ── Scene definitions ──────────────────────────────────────────────

SCENES = {
    "gui/assembly_bench.png": {
        "size": (176, 174),
        "elements": [
            {"type": "window"},
            {"type": "slot", "x": 44, "y": 35},      # paper
            {"type": "slot", "x": 62, "y": 35},      # gunpowder
            {"type": "slot_grid", "x": 98, "y": 17, "cols": 4, "rows": 2},  # 8 stars
            {"type": "slot", "x": 152, "y": 35},     # result
            {"type": "player_inv", "y": 84},
        ],
    },
    "gui/effect_bench.png": {
        "size": (176, 174),
        "elements": [
            {"type": "window"},
            {"type": "slot", "x": 44, "y": 35},      # star input
            {"type": "slot", "x": 26, "y": 57},      # shape modifier
            {"type": "slot", "x": 62, "y": 57},      # diamond (trail)
            {"type": "slot", "x": 80, "y": 57},      # glowstone (twinkle)
            {"type": "slot", "x": 134, "y": 35},     # result
            {"type": "player_inv", "y": 84},
        ],
    },
    "gui/color_vat.png": {
        "size": (176, 207),
        "elements": [
            {"type": "window"},
            {"type": "slot_grid", "x": 26, "y": 17, "cols": 4, "rows": 4},  # 16 dyes
            {"type": "slot", "x": 26, "y": 93},      # gunpowder
            {"type": "slot", "x": 134, "y": 53},     # result
            {"type": "label", "x": 62, "y": 78, "text": "Base"},
            {"type": "label", "x": 110, "y": 78, "text": "Fade"},
            {"type": "player_inv", "y": 125},
        ],
    },
}

# ── Generate ──────────────────────────────────────────────────────

BASE = "src/main/resources/assets/pyrotechnics/textures"

block_textures = {
    f"{BASE}/block/color_vat_top.png":      make_color_vat_top(),
    f"{BASE}/block/color_vat.png":          make_color_vat_side(),
    f"{BASE}/block/effect_bench_top.png":   make_effect_bench_top(),
    f"{BASE}/block/effect_bench.png":       make_effect_bench_side(),
    f"{BASE}/block/assembly_bench_top.png": make_assembly_bench_top(),
    f"{BASE}/block/assembly_bench.png":     make_assembly_bench_side(),
}

for path, img in block_textures.items():
    os.makedirs(os.path.dirname(path), exist_ok=True)
    img.save(path)
    print(f"  {path}  ({img.size[0]}×{img.size[1]})")

for filename, scene in SCENES.items():
    path = f"{BASE}/{filename}"
    os.makedirs(os.path.dirname(path), exist_ok=True)
    img = render(scene)
    img.save(path)
    print(f"  {path}  ({img.size[0]}×{img.size[1]})")

print("Done!")
