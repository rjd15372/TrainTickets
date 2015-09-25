#pragma once

typedef enum {
    INIT, RUNNING
} protocol_state;

typedef struct {
    char trip[32];
    char seat[32];
} ticket_info;

typedef struct {
    char sec_title[32];
    int8_t num_tickets;
    ticket_info *tickets;
} ticket_section;

typedef struct {
    protocol_state state;
    int8_t total_sections;
    int8_t total_tickets;
    int8_t curr_section;
    int8_t curr_ticket;
    ticket_section *sections;
} ticket_protocol;


ticket_protocol *init_protocol();

void start_protocol(ticket_protocol *prt);

int handle_received_message(ticket_protocol *prt, DictionaryIterator *ptr);