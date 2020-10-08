(ns lanterna.constants
  (:import java.nio.charset.Charset
           com.googlecode.lanterna.input.KeyType
           com.googlecode.lanterna.TextColor$ANSI
           com.googlecode.lanterna.SGR
           ;; com.googlecode.lanterna.terminal.swing.TerminalPalette
           ))

(def charsets {:utf-8 (Charset/forName "UTF-8")})

(def colors
  {:black   TextColor$ANSI/BLACK
   :white   TextColor$ANSI/WHITE
   :red     TextColor$ANSI/RED
   :green   TextColor$ANSI/GREEN
   :blue    TextColor$ANSI/BLUE
   :cyan    TextColor$ANSI/CYAN
   :magenta TextColor$ANSI/MAGENTA
   :yellow  TextColor$ANSI/YELLOW
   :default TextColor$ANSI/DEFAULT})

(def styles
  {:bold SGR/BOLD
   :reverse SGR/REVERSE
   :underline SGR/UNDERLINE
   :blinking SGR/BLINK})

(def key-codes
  {;; KeyType/NormalKey :normal
   KeyType/Escape :escape
   KeyType/Backspace :backspace
   KeyType/ArrowLeft :left
   KeyType/ArrowRight :right
   KeyType/ArrowUp :up
   KeyType/ArrowDown :down
   KeyType/Insert :insert
   KeyType/Delete :delete
   KeyType/Home :home
   KeyType/End :end
   KeyType/PageUp :page-up
   KeyType/PageDown :page-down
   KeyType/Tab :tab
   KeyType/ReverseTab :reverse-tab
   KeyType/Enter :enter
   KeyType/Unknown :unknown
   KeyType/CursorLocation :cursor-location})

;; (def palettes
;;   {:gnome      TerminalPalette/GNOME_TERMINAL
;;    :vga        TerminalPalette/STANDARD_VGA
;;    :windows-xp TerminalPalette/WINDOWS_XP_COMMAND_PROMPT
;;    :mac-os-x   TerminalPalette/MAC_OS_X_TERMINAL_APP
;;    :xterm      TerminalPalette/PUTTY
;;    :putty      TerminalPalette/XTERM})

;; (def enter-styles
;;   {:bold com.googlecode.lanterna.terminal.Terminal$SGR/ENTER_BOLD
;;    :reverse com.googlecode.lanterna.terminal.Terminal$SGR/ENTER_REVERSE
;;    :blinking com.googlecode.lanterna.terminal.Terminal$SGR/ENTER_BLINK
;;    :underline com.googlecode.lanterna.terminal.Terminal$SGR/ENTER_UNDERLINE})

;; (def exit-styles
;;   {:bold com.googlecode.lanterna.terminal.Terminal$SGR/EXIT_BOLD
;;    :reverse com.googlecode.lanterna.terminal.Terminal$SGR/EXIT_REVERSE
;;    :blinking com.googlecode.lanterna.terminal.Terminal$SGR/EXIT_BLINK
;;    :underline com.googlecode.lanterna.terminal.Terminal$SGR/EXIT_UNDERLINE})

;; (def reset-style
;;   com.googlecode.lanterna.terminal.Terminal$SGR/RESET_ALL)
