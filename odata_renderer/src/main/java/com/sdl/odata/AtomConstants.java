/*
 * Copyright (c) 2014-2021 All Rights Reserved by the RWS Group for and on behalf of its affiliates and subsidiaries.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sdl.odata;

/**
 * OData v4 Atom Xml related constants.
 */
public final class AtomConstants {
    private AtomConstants() {
    }

    /**
     * XML Version.
     */
    public static final String XML_VERSION = "1.0";
    /**
     * ID.
     */
    public static final String ID = "id";
    /**
     * Title.
     */
    public static final String TITLE = "title";
    /**
     * Count.
     */
    public static final String COUNT = "count";
    /**
     * Type.
     */
    public static final String TYPE = "type";
    /**
     * REL.
     */
    public static final String REL = "rel";
    /**
     * REF.
     */
    public static final String REF = "ref";
    /**
     * HREF.
     */
    public static final String HREF = "href";
    /**
     * Term.
     */
    public static final String TERM = "term";
    /**
     * Hash.
     */
    public static final String HASH = "#";
    /**
     * Null.
     */
    public static final String NULL = "null";
    /**
     * Collection.
     */
    public static final String COLLECTION = "Collection";
    /**
     * Element.
     */
    public static final String ELEMENT = "element";
    /**
     * Value.
     */
    public static final String VALUE = "value";
    /**
     * Edit.
     */
    public static final String EDIT = "edit";
    /**
     * Self.
     */
    public static final String SELF = "self";
    /**
     * Metadata.
     */
    public static final String METADATA = "metadata";
    /**
     * Inline.
     */
    public static final String INLINE = "inline";
    /**
     * Scheme.
     */
    public static final String SCHEME = "scheme";

    /**
     * Atom_ns.
     */
    public static final String ATOM_NS = "http://www.w3.org/2005/Atom";
    /**
     * Atom feed.
     */
    public static final String ATOM_FEED = "feed";
    /**
     * Atom entry.
     */
    public static final String ATOM_ENTRY = "entry";
    /**
     * Atom ID.
     */
    public static final String ATOM_ID = "id";
    /**
     * Atom Summary.
     */
    public static final String ATOM_SUMMARY = "summary";
    /**
     * Atom Updated.
     */
    public static final String ATOM_UPDATED = "updated";
    /**
     * Atom Author.
     */
    public static final String ATOM_AUTHOR = "author";
    /**
     * Atom Name.
     */
    public static final String ATOM_NAME = "name";
    /**
     * Atom Link.
     */
    public static final String ATOM_LINK = "link";
    /**
     * Atom Category.
     */
    public static final String ATOM_CATEGORY = "category";
    /**
     * Atom Author Odata Framework.
     */
    public static final String ATOM_AUTHOR_ODATA_FRAMEWORK = "Tridion OData v4 framework";

    /**
     * The position of the feed {@code <id>} element in a XML fragment containing a feed.
     */
    public static final int FEED_METADATA_ATOM_ID_IDX = 0;

    /**
     * The position of the feed {@code <title>} element in a XML fragment containing a feed.
     */
    public static final int FEED_METADATA_TITLE_IDX = 1;

    /**
     * The position of the feed {@code <updated>} element in a XML fragment containing a feed.
     */
    public static final int FEED_METADATA_UPDATED_IDX = 2;

    /**
     * The position of the feed {@code <link>} element in a XML fragment containing a feed.
     */
    public static final int FEED_METADATA_LINK_IDX = 3;

    /**
     * The minimum number of feed metadata elements that should appear in a XML fragment containing a feed.
     */
    public static final int FEED_METADATA_MIN_ITEMS = 4;

    /**
     * OData Scheme NS.
     */
    public static final String ODATA_SCHEME_NS = "http://docs.oasis-open.org/odata/ns/scheme";
    /**
     * OData Metadata NS.
     */
    public static final String ODATA_METADATA_NS = "http://docs.oasis-open.org/odata/ns/metadata";
    /**
     * ODATA Data.
     */
    public static final String ODATA_DATA = "data";
    /**
     * OData Data NS.
     */
    public static final String ODATA_DATA_NS = "http://docs.oasis-open.org/odata/ns/data";

    /**
     * OData Navigation Link REL NS Prefix.
     */
    public static final String ODATA_NAVIGATION_LINK_REL_NS_PREFIX = "http://docs.oasis-open.org/odata/ns/related/";
    /**
     * OData Association Link Rel NS Prefix.
     */
    public static final String ODATA_ASSOCIATION_LINK_REL_NS_PREFIX =
            "http://docs.oasis-open.org/odata/ns/relatedlinks/";
    /**
     * OData Feed Link Type Pattern.
     */
    public static final String ODATA_FEED_LINK_TYPE_PATTERN = "%s;type=feed";
    /**
     * OData Entry Link Type Pattern.
     */
    public static final String ODATA_ENTRY_LINK_TYPE_PATTERN = "%s;type=entry";
    /**
     * OData Xml Base.
     */
    public static final String ODATA_XML_BASE = "xml:base";
    /**
     * OData Context.
     */
    public static final String ODATA_CONTEXT = "context";
    /**
     * OData Content.
     */
    public static final String ODATA_CONTENT = "content";
    /**
     * OData Properties.
     */
    public static final String ODATA_PROPERTIES = "properties";
}
