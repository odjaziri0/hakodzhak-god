from PIL import Image, ImageDraw
img = Image.new('RGB', (512, 512), color='#0A0A0A')
draw = ImageDraw.Draw(img)
draw.ellipse([106, 106, 406, 406], outline='#FF0000', width=15)
draw.ellipse([176, 206, 256, 286], fill='#FF0000')
draw.ellipse([256, 206, 336, 286], fill='#FF0000')
draw.rectangle([196, 336, 316, 356], fill='#8B0000')
img.save('app/src/main/res/drawable/ic_launcher.png')
