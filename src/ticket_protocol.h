#pragma once

typedef enum {INIT, RUNNING} protocol_state;

typedef struct {
  char trip[32];
  char seat[32];
} ticket_info;

typedef struct {
  char sec_title[32];
  int num_tickets;
  ticket_info *tickets;
} ticket_section;

typedef struct {
  protocol_state state;
  int total_sections;
  int total_tickets;
  int curr_section;
  int curr_ticket;
  ticket_section *sections;
} ticket_protocol;


ticket_protocol *init_protocol();
void start_protocol(ticket_protocol *prt);
void handle_received_message(ticket_protocol *prt);