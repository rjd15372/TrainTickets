#include <pebble.h>
#include "ticket_protocol.h"

// KEYS
#define HANDSHAKE_KEY 0
#define SECTION_OFFSET_KEY 96
#define TICKET_OFFSET_KEY 97


// VALUES
#define PROTOCOL_STATE_INIT 100
#define PROTOCOL_STATE_RUNNING 101

int handle_init_message(ticket_protocol *prt, DictionaryIterator *iterator);

int handle_running_message(ticket_protocol *prt, DictionaryIterator *iterator);

ticket_protocol *init_protocol() {
    ticket_protocol *prt = (ticket_protocol *) malloc(sizeof(ticket_protocol));
    prt->state = INIT;
    prt->total_sections = 0;
    prt->total_tickets = 0;
    prt->curr_section = 0;
    prt->curr_ticket = 0;
    prt->sections = NULL;
    return prt;
}



void start_protocol(ticket_protocol *prt) {
    DictionaryIterator *iterator;

    app_message_outbox_begin(&iterator);

    int8_t value = PROTOCOL_STATE_INIT;
    dict_write_int8(iterator, HANDSHAKE_KEY, value);

    prt->state = RUNNING;

    if (prt->sections != NULL) {
        free(prt->sections);
    }

    app_message_outbox_send();
}


void send_protocol_message(ticket_protocol *prt) {
    DictionaryIterator *iterator;

    app_message_outbox_begin(&iterator);

    int8_t value = PROTOCOL_STATE_RUNNING;
    dict_write_int8(iterator, HANDSHAKE_KEY, value);
    dict_write_int8(iterator, SECTION_OFFSET_KEY, prt->curr_section);
    dict_write_int8(iterator, TICKET_OFFSET_KEY, prt->curr_ticket);

    app_message_outbox_send();
}

int handle_init_message(ticket_protocol *prt, DictionaryIterator *iterator) {
    Tuple *tuple;

    tuple = dict_find(iterator, 1);
    if (tuple == NULL) return -2;
    prt->total_sections = tuple->value->int8;

    tuple = dict_find(iterator, 2);
    if (tuple == NULL) return -2;
    prt->total_tickets = tuple->value->int8;

    prt->sections = (ticket_section *)malloc(sizeof(ticket_section)*prt->total_sections);
    memset(prt->sections, 0, sizeof(ticket_section)*prt->total_sections);

    prt->state = RUNNING;

    send_protocol_message(prt);

    return 0;
}

int handle_running_message(ticket_protocol *prt, DictionaryIterator *iterator) {
    Tuple *tuple;

    ticket_section *section = &prt->sections[prt->curr_section];

    if (prt->curr_ticket == 0) {
        tuple = dict_find(iterator, 1);
        if (tuple == NULL) return -2;
        int num_tickets = tuple->value->int8;

        section->num_tickets = num_tickets;

        tuple = dict_find(iterator, 3);
        if (tuple == NULL) return -2;
        snprintf(section->sec_title, 32, "%s", tuple->value->cstring);

        section->tickets = (ticket_info *)malloc(sizeof(ticket_info)*num_tickets);
        memset(section->tickets, 0, sizeof(ticket_info)*num_tickets);
    }


    tuple = dict_find(iterator, 2);
    if (tuple == NULL) return -2;
    int sent_tickets = tuple->value->int8;

    int n = 0;
    uint32_t key_offset = 3;

    while(n < sent_tickets) {
        ticket_info *ticket = &section->tickets[prt->curr_ticket];

        tuple = dict_find(iterator, key_offset);
        key_offset++;

        if (tuple == NULL) return -2;
        snprintf(ticket->trip, 32, "%s", tuple->value->cstring);

        tuple = dict_find(iterator, key_offset);
        key_offset++;

        if (tuple == NULL) return -2;
        int car = tuple->value->int8;

        tuple = dict_find(iterator, key_offset);
        key_offset++;
        if (tuple == NULL) return -2;
        int seat = tuple->value->int8;

        snprintf(ticket->seat, 32, "Car:%d  Lug:%d", car, seat);

        prt->curr_ticket++;
        n++;
    }

    if (prt->curr_ticket == section->num_tickets) {
        prt->curr_ticket = 0;
        prt->curr_section++;
    }

    if (prt->curr_section == prt->total_sections) {
        prt->curr_section = 0;
        prt->state = INIT;
        return 1;
    }

    send_protocol_message(prt);

    return 0;

}


int handle_received_message(ticket_protocol *prt, DictionaryIterator *iterator) {

    Tuple *tuple = dict_find(iterator, HANDSHAKE_KEY);
    if (tuple == NULL) return -1;

    if (tuple->value->int8 == PROTOCOL_STATE_INIT) {
        return handle_init_message(prt, iterator);
    }
    else if (tuple->value->int8 == PROTOCOL_STATE_RUNNING) {
        return handle_running_message(prt, iterator);
    }

    return -1;
}


