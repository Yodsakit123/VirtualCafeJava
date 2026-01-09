# ☕ Royal Cafe POS System - User Guide

## 🎉 What's New?

You now have a **brand new Point of Sale (POS) system** with a modern, professional interface inspired by real cafe systems!

## 🌟 Features

### 📋 Visual Menu System
- **4 Categories**: Coffee, Food, Desserts, Drinks
- **24 Menu Items** with colorful icons
- **Grid Layout** - 3 items per row for easy browsing
- **Touch-friendly cards** with hover effects
- **Clear pricing** displayed on each item

### 🛒 Shopping Cart
- **Real-time order tracking** on the right panel
- **Quantity controls** (+/- buttons for each item)
- **Running totals** (Subtotal, Tax, Total)
- **Customer name** field
- **10% Tax** automatically calculated

### 💰 Professional Design
- **Coffee shop color scheme** (brown header, clean white)
- **Large touch-friendly buttons**
- **Smooth animations** and hover effects
- **Clean, organized layout**
- **Easy to use** for staff and customers

## 🚀 How to Use

### Setup:

1. **Place CafePOS.java in src folder**:
   ```
   VirtualCafeJava/src/CafePOS.java
   ```

2. **Recompile** (run `1-compile.bat`)

3. **Make sure server is running** (`2-run-server.bat`)

4. **Launch POS** (run `6-run-cafe-pos.bat`)

### Taking Orders:

1. **Enter customer name** in the "Customer:" field

2. **Browse menu** by clicking tabs:
   - Coffee ☕
   - Food 🥐
   - Desserts 🍰
   - Drinks 🥤

3. **Click "+ ADD"** on items to add to cart

4. **Adjust quantities**:
   - Click **"+"** to increase
   - Click **"-"** to decrease
   - Item removes at quantity 0

5. **Review order** in right panel:
   - Check items and quantities
   - View subtotal, tax, and total

6. **Place Order**:
   - Click **"PLACE ORDER"** button
   - See confirmation message
   - Cart automatically clears

7. **Start next order!**

### Additional Features:

- **Clear Cart**: Red "Clear" button at top of cart
- **Cancel Order**: Gray "Cancel Order" button at bottom
- **Multiple Items**: Add as many items as needed
- **Same Item Multiple Times**: Automatically increases quantity

## 📊 System Architecture

```
Your Complete System Now:

┌─────────────────────────────────────┐
│        Customer Interfaces          │
├─────────────────────────────────────┤
│  1. CafePOS (NEW!)      ⭐         │  Customer ordering
│  2. CustomerGUI          ✓          │  Admin/management
│  3. CustomerCLI          ✓          │  Quick testing
└─────────────────────────────────────┘
                 │
                 ↓
┌─────────────────────────────────────┐
│      BaristaServer (Port 5050)      │  Backend
└─────────────────────────────────────┘
                 │
                 ↓
┌─────────────────────────────────────┐
│     BaristaDashboard       ✓        │  Kitchen workflow
└─────────────────────────────────────┘
```

## 🎨 Menu Customization

Want to change menu items? Edit `CafePOS.java`:

### Add New Item:
```java
coffee.add(new MenuItem("Flat White", 3.80, "☕", "#DEB887"));
//                      Name         Price  Icon  Color
```

### Add New Category:
```java
List<MenuItem> breakfast = new ArrayList<>();
breakfast.add(new MenuItem("Pancakes", 6.00, "🥞", "#FFD700"));
MENU.put("Breakfast", breakfast);
```

### Change Tax Rate:
```java
private static final double TAX_RATE = 0.10; // Change to 0.15 for 15%
```

## 💡 Pro Tips

### For Fast Service:
1. Keep POS open full-screen
2. Use keyboard: Tab to navigate, Enter to add
3. Have server running before opening POS
4. Train staff on menu categories

### For Busy Times:
1. Open multiple POS windows for multiple cashiers
2. Use Barista Dashboard in kitchen to track orders
3. Customer names help identify orders
4. Orders appear immediately in dashboard

### For Customization:
1. Edit menu items in the code
2. Change colors by modifying styles
3. Add more categories as needed
4. Adjust tax rate for your region

## 🔧 Troubleshooting

### POS won't open:
- Run `1-compile.bat` first
- Make sure JavaFX is in lib folder
- Check server is running

### Orders not appearing:
- Verify server is running (port 5050)
- Check Barista Dashboard to confirm
- Look at server terminal for errors

### Cart issues:
- Click "Clear" to reset
- Close and reopen POS if stuck
- Check customer name is entered

### Menu items missing:
- Recompile after editing menu
- Check for syntax errors
- Verify all MenuItem entries are correct

## 📈 Comparison: Old vs New

| Feature | CustomerGUI | **CafePOS (NEW)** |
|---------|-------------|-------------------|
| Visual Menu | ❌ | ✅ Colorful grid |
| Categories | ❌ | ✅ 4 tabs |
| Icons/Emojis | ❌ | ✅ Every item |
| Shopping Cart | ❌ | ✅ Real-time |
| Tax Calculation | ❌ | ✅ Auto 10% |
| Touch-Friendly | ⚠️ | ✅ Large buttons |
| Modern Design | ⚠️ | ✅ Professional |
| Order History | ✅ | ❌ (use CustomerGUI) |
| Status Updates | ✅ | ❌ (use Dashboard) |

**Use CafePOS for**: Taking new orders quickly
**Use CustomerGUI for**: Managing existing orders
**Use BaristaDashboard for**: Kitchen workflow

## 🎯 Best Practices

1. **CafePOS** → Customer-facing ordering station
2. **BaristaDashboard** → Kitchen display (shows order status)
3. **CustomerGUI** → Back office (view all orders, manage)
4. **CustomerCLI** → Testing and troubleshooting

## 🌟 What's Different from Reference Image?

Your POS includes:
- ✅ Visual menu grid layout
- ✅ Shopping cart with quantities
- ✅ Tax calculation
- ✅ Professional design
- ✅ Category tabs
- ✅ Modern color scheme

Plus added features:
- 🎨 Hover effects on menu items
- 📱 Responsive layout
- 🎯 Clean, touch-friendly interface
- 💚 Success confirmations
- 🧹 Easy cart management

## 🚀 Next Steps

1. **Customize the menu** with your actual items
2. **Adjust colors** to match your branding
3. **Train staff** on the new system
4. **Set up tablets** for customer self-ordering
5. **Add more features** as needed (loyalty program, discounts, etc.)

---

**Enjoy your new Professional POS System! ☕✨**

Need help? All your other tools still work:
- BaristaDashboard for kitchen
- CustomerGUI for management  
- Server saves all orders to data.json
