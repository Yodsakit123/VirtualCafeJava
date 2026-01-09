# ☕ Virtual Cafe Java - Batch Files Guide

## 📦 Batch Files Included

All batch files are numbered in the order you should use them:

1. **1-compile.bat** - Compiles all Java source files
2. **2-run-server.bat** - Starts the backend server
3. **3-run-customer-cli.bat** - Runs the command-line customer interface
4. **4-run-customer-gui.bat** - Runs the graphical customer interface
5. **5-run-barista-dashboard.bat** - Runs the barista dashboard
6. **RUN-ALL.bat** - Launches everything at once (except compile)

## 🚀 Quick Start

### First Time Setup:

1. Make sure all `.bat` files are in your **VirtualCafeJava** root folder
2. Double-click **1-compile.bat**
3. Wait for "Compilation Successful!" message

### Running the System:

**Option A: Run Everything at Once**
- Double-click **RUN-ALL.bat**
- Four windows will open automatically
- Keep the "Barista Server" window open

**Option B: Run Individually**
1. Double-click **2-run-server.bat** (keep this open!)
2. Double-click **3-run-customer-cli.bat** (for text interface)
3. Double-click **4-run-customer-gui.bat** (for graphical interface)
4. Double-click **5-run-barista-dashboard.bat** (for barista workflow)

## 📝 Usage Tips

### Compilation (1-compile.bat)
- Run this whenever you modify any `.java` files
- Creates `.class` files in the `bin` folder
- Must complete successfully before running applications

### Server (2-run-server.bat)
- **MUST run first** before any client applications
- Shows "[Server] Listening on port 5050" when ready
- Keep this window **open** while using the system
- Press `Ctrl+C` to stop the server
- Data is saved to `data.json` automatically

### Customer CLI (3-run-customer-cli.bat)
- Text-based interface for quick testing
- Menu options:
  - `1` - List all orders
  - `2` - Place a new order
  - `3` - Update order status
  - `0` - Exit

### Customer GUI (4-run-customer-gui.bat)
- Graphical interface with table view
- Features:
  - View all orders in a table
  - Place orders with a form
  - Update order status
  - Color-coded status indicators
- Best for customer-facing operations

### Barista Dashboard (5-run-barista-dashboard.bat)
- Kanban-style dashboard for baristas
- Three columns:
  - **Pending** - New orders waiting
  - **In Progress** - Currently being prepared
  - **Served** - Completed orders
- Auto-refreshes every 5 seconds
- Click buttons to move orders between stages

## ⚠️ Troubleshooting

### "javac is not recognized"
- Java JDK not installed or not in PATH
- Install Java 17+ from https://adoptium.net/
- Make sure to add to PATH during installation

### "JavaFX SDK not found"
- Extract JavaFX to the `lib` folder
- Should look like: `lib\javafx-sdk-25.0.1\`

### "BaristaServer.class not found"
- You need to compile first
- Run **1-compile.bat** before running applications

### "Connection refused" in clients
- Server is not running
- Start **2-run-server.bat** first
- Wait for "Listening on port 5050" message

### GUI windows are blank or frozen
- Check server terminal for errors
- Make sure Java 17+ is installed
- Try restarting the server

### "Port 5050 already in use"
- Another instance is running
- Close all server windows
- Or use Task Manager to end Java processes

## 🔄 Development Workflow

When making changes to the code:

1. Stop all running applications (close all windows)
2. Modify your `.java` files in the `src` folder
3. Run **1-compile.bat** to recompile
4. Restart **2-run-server.bat**
5. Run your client applications again

## 💾 Data Persistence

- Orders are saved to `data.json` in the project root
- Created automatically on first order
- Persists between server restarts
- To reset data: delete `data.json` and restart server

## 🎯 System Architecture

```
┌─────────────────┐
│  Batch Files    │
└────────┬────────┘
         │
    ┌────▼─────┬──────────┬──────────┐
    │          │          │          │
┌───▼───┐  ┌──▼──┐   ┌───▼────┐  ┌──▼─────────┐
│Server │  │ CLI │   │Customer│  │  Barista   │
│:5050  │  │     │   │  GUI   │  │ Dashboard  │
└───┬───┘  └──┬──┘   └───┬────┘  └──┬─────────┘
    │         │          │          │
    └─────────┴──────────┴──────────┘
              │
         ┌────▼────┐
         │data.json│
         └─────────┘
```

## ✅ Success Checklist

- [ ] All batch files in VirtualCafeJava root folder
- [ ] Java 17+ installed (`java -version`)
- [ ] JavaFX SDK in lib folder
- [ ] Gson JAR in lib folder
- [ ] Compiled successfully (1-compile.bat)
- [ ] Server starts without errors
- [ ] Clients can connect
- [ ] Can place and view orders

## 🎓 Need Help?

If you encounter issues:
1. Check the error messages in the terminal windows
2. Verify all prerequisites are installed
3. Make sure you're running batch files from the project root
4. Ensure no other application is using port 5050

---

**Enjoy your Virtual Cafe System! ☕**
