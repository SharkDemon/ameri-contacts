package com.simco.americontacts.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.simco.americontacts.model.Contact;

@Controller
@SessionAttributes("contacts")
public class ContactsController {

    private static final Logger logger = LoggerFactory.getLogger(ContactsController.class);

    // keep the list of contact objects in the session
    @ModelAttribute("contacts")
    public List<Contact> contactList() {
        return null;
    }

    @GetMapping("/")
    public String showContacts(
            @ModelAttribute("contacts") List<Contact> currentContacts,
            Model model) {

        // ensure the contacts list has been initialized, and we'll populate it
        // with some dummy data
        if (null == currentContacts) {
            currentContacts = new ArrayList<Contact>(3);
            currentContacts.add( new Contact(UUID.randomUUID(), "Jim", "Carrey", "jim.carrey@carrey.org") );
            currentContacts.add( new Contact(UUID.randomUUID(), "James", "Kirk", "captain.kirk@enterprise.org") );
            currentContacts.add( new Contact(UUID.randomUUID(), "Jackie", "Chan", "jchan@chan.com") );
        }

        logger.info("showContacts invoked - listing [{}] contacts", currentContacts.size());

        model.addAttribute("contacts", currentContacts);
        model.addAttribute("newContact", new Contact());
        return "index";
    }

    @PostMapping("/addContact")
    public ModelAndView addContact(
            @ModelAttribute("contacts") List<Contact> currentContacts,
            @ModelAttribute Contact newContact,
            BindingResult errors,
            ModelMap model) {

        logger.info("addContact invoked - firstName=[{}], lastName=[{}], email=[{}]",
                newContact.getFirstName(),
                newContact.getLastName(),
                newContact.getEmail());

        // assign ID to the new contact and add it to our collection
        newContact.setId(UUID.randomUUID());
        currentContacts.add(newContact);

        model.addAttribute("contacts", currentContacts);
        return new ModelAndView("redirect:/", model);
    }

    @GetMapping("/editContact/{contactId}")
    public String showEditContact(
            @ModelAttribute("contacts") List<Contact> currentContacts,
            @PathVariable UUID contactId,
            ModelMap model) {

        logger.info("showEditContact invoked - contactId=[{}]", contactId);

        // get the contact matching on UUID
        Contact contactToEdit = currentContacts.stream()
                .filter(cc -> cc.getId().equals(contactId))
                .collect(Collectors.toList())
                .get(0); // let's assume a find

        model.addAttribute("contacts", currentContacts);
        model.addAttribute("contactToEdit", contactToEdit);
        return "editContact";
    }

    @PostMapping("/editContact/{contactId}")
    public ModelAndView editContact(
            @ModelAttribute("contacts") List<Contact> currentContacts,
            @ModelAttribute Contact editedContact,
            @PathVariable UUID contactId,
            ModelMap model) {

        logger.info("editContact invoked - id=[{}], firstName=[{}], lastName=[{}], email=[{}]",
                contactId,
                editedContact.getFirstName(),
                editedContact.getLastName(),
                editedContact.getEmail());

        // apply id to the submitted object, since the id was not passed via
        // the HTML fields
        editedContact.setId(contactId);

        // insert the submitted contact into our contacts list from session
        int indexOfInterest = 0;
        for (Contact c : currentContacts) {
            if (c.getId().equals(contactId))
                break;
            indexOfInterest++;
        }
        currentContacts.set(indexOfInterest, editedContact);

        model.addAttribute("contacts", currentContacts);
        return new ModelAndView("redirect:/", model);
    }

    @GetMapping("/removeContact/{contactId}")
    public ModelAndView removeContact(
            @ModelAttribute("contacts") List<Contact> currentContacts,
            @PathVariable UUID contactId,
            ModelMap model) {

        logger.info("removeContact invoked - contactId=[{}]", contactId);

        // remove contacts matching on UUID
        currentContacts = currentContacts.stream()
                .filter(cc -> !cc.getId().equals(contactId))
                .collect(Collectors.toList());

        model.addAttribute("contacts", currentContacts);
        return new ModelAndView("redirect:/", model);
    }

}
