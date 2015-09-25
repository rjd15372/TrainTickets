#include <pebble.h>
  
#include "ticket_protocol.h"

ticket_protocol *protocol;
  
static Window *window;
static TextLayer *text_layer;
static MenuLayer *s_menu_layer;


static uint16_t menu_get_num_sections_callback(MenuLayer *menu_layer, void *data) {
  return protocol->total_sections;
}

static uint16_t menu_get_num_rows_callback(MenuLayer *menu_layer, uint16_t section_index, void *data) {
  return protocol->sections[section_index].num_tickets;
}

static int16_t menu_get_header_height_callback(MenuLayer *menu_layer, uint16_t section_index, void *data) {
  return MENU_CELL_BASIC_HEADER_HEIGHT;
}

static void menu_draw_header_callback(GContext* ctx, const Layer *cell_layer, uint16_t section_index, void *data) {
  menu_cell_basic_header_draw(ctx, cell_layer, protocol->section[section_index].title);
}

static void menu_draw_row_callback(GContext* ctx, const Layer *cell_layer, MenuIndex *cell_index, void *data) {
  ticket_info *info = &(protocol->sections[cell_index->section].tickets[cell_index->row]);
  menu_cell_basic_draw(ctx, cell_layer, info->trip, info->seat, NULL);
}

static void menu_select_callback(MenuLayer *menu_layer, MenuIndex *cell_index, void *data) {
  start_protocol(protocol);
}

static void inbox_received_callback(DictionaryIterator *iterator, void *context) {
    
  handle_received_message(protocol);
  
}

static void inbox_dropped_callback(AppMessageResult reason, void *context) {
   /* APP_LOG(APP_LOG_LEVEL_INFO, "Message dropped!");
  switch(reason){
    case APP_MSG_OK:
      APP_LOG(APP_LOG_LEVEL_INFO, "%s","APP_MSG_OK");
    break;
    case APP_MSG_SEND_TIMEOUT:
      APP_LOG(APP_LOG_LEVEL_INFO, "%s","SEND TIMEOUT");
    break;
    case APP_MSG_SEND_REJECTED:
      APP_LOG(APP_LOG_LEVEL_INFO, "%s","SEND REJECTED");
    break;
    case APP_MSG_NOT_CONNECTED:
      APP_LOG(APP_LOG_LEVEL_INFO, "%s","NOT CONNECTED");
    break;
    case APP_MSG_APP_NOT_RUNNING:
      APP_LOG(APP_LOG_LEVEL_INFO, "%s","NOT RUNNING");
    break;
    case APP_MSG_INVALID_ARGS:
      APP_LOG(APP_LOG_LEVEL_INFO, "%s","INVALID ARGS");
    break;
    case APP_MSG_BUSY:
      APP_LOG(APP_LOG_LEVEL_INFO, "%s","BUSY");
    break;
    case APP_MSG_BUFFER_OVERFLOW:
      APP_LOG(APP_LOG_LEVEL_INFO, "%s","BUFFER OVERFLOW");
    break;
    case APP_MSG_ALREADY_RELEASED:
      APP_LOG(APP_LOG_LEVEL_INFO, "%s","ALRDY RELEASED");
    break;
    case APP_MSG_CALLBACK_ALREADY_REGISTERED:
      APP_LOG(APP_LOG_LEVEL_INFO, "%s","CLB ALR REG");
    break;
    case APP_MSG_CALLBACK_NOT_REGISTERED:
      APP_LOG(APP_LOG_LEVEL_INFO, "%s","CLB NOT REG");
    break;
    case APP_MSG_OUT_OF_MEMORY:
      APP_LOG(APP_LOG_LEVEL_INFO, "%s","OUT OF MEM");
      break;
    case APP_MSG_CLOSED:
      APP_LOG(APP_LOG_LEVEL_INFO, "%s","MSG CLOSED");
      break;
    case APP_MSG_INTERNAL_ERROR: 
      APP_LOG(APP_LOG_LEVEL_INFO, "%s","INTERNAL ERROR");
      break;
  }*/
}

static void outbox_failed_callback(DictionaryIterator *iterator, AppMessageResult reason, void *context) {
    //APP_LOG(APP_LOG_LEVEL_INFO , "Outbox send failed!");
}

static void outbox_sent_callback(DictionaryIterator *iterator, void *context) {
    //APP_LOG(APP_LOG_LEVEL_INFO , "Outbox send success!");
}


static void main_window_load(Window *window) {
  // Now we prepare to initialize the menu layer
  Layer *window_layer = window_get_root_layer(window);
  GRect bounds = layer_get_frame(window_layer);
  
  s_menu_layer = menu_layer_create(bounds);
  menu_layer_set_callbacks(s_menu_layer, NULL, (MenuLayerCallbacks){
    .get_num_sections = menu_get_num_sections_callback,
    .get_num_rows = menu_get_num_rows_callback,
    .get_header_height = menu_get_header_height_callback,
    .draw_header = menu_draw_header_callback,
    .draw_row = menu_draw_row_callback,
    .select_click = menu_select_callback,
  });
  
  // Bind the menu layer's click config provider to the window for interactivity
  menu_layer_set_click_config_onto_window(s_menu_layer, window);

  layer_add_child(window_layer, menu_layer_get_layer(s_menu_layer));
}

static void main_window_unload(Window *window) {
  // Destroy the menu layer
  menu_layer_destroy(s_menu_layer);

}

void handle_init(void) {
  protocol = init_protocol();
  
	// Create a window and text layer
	window = window_create();
  window_set_window_handlers(window, (WindowHandlers) {
    .load = main_window_load,
    .unload = main_window_unload,
  });
	
	// Push the window
	window_stack_push(window, true);
  
  // Register callbacks
  app_message_register_inbox_received(inbox_received_callback);
  app_message_register_inbox_dropped(inbox_dropped_callback);
  app_message_register_outbox_failed(outbox_failed_callback);
  app_message_register_outbox_sent(outbox_sent_callback);
  
  // Open AppMessage
  app_message_open(app_message_inbox_size_maximum(), app_message_outbox_size_maximum());
  
  start_protocol(protocol);
  
}

void handle_deinit(void) {
	// Destroy the text layer
	text_layer_destroy(text_layer);
	
	// Destroy the window
	window_destroy(window);
  
  free(protocol);
}

int main(void) {
	handle_init();
	app_event_loop();
	handle_deinit();
}
